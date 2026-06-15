package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;

@Repository
public interface NetworkConnectionRepository extends JpaRepository<NetworkConnection, Long> {

   // These Retrievers are to be used by the rest api
   List<NetworkConnection> findByLinkType(LinkType linkType);

   List<NetworkConnection> findByStartId(Long startId);

   List<NetworkConnection> findByEndId(Long endId);

   List<NetworkConnection> findByStartIdOrEndId(Long startId, Long endId);

   // paginated retrievers for rest api
   Page<NetworkConnection> findAll(Pageable pageable);

   Page<NetworkConnection> findByLinkType(LinkType linkType, Pageable pageable);

   Page<NetworkConnection> findByStartId(Long startId, Pageable pageable);

   Page<NetworkConnection> findByEndId(Long endId, Pageable pageable);

   Page<NetworkConnection> findByStartIdOrEndId(Long startId, Long endId, Pageable pageable);
   
   Page<NetworkConnection> findByStreetNameIsNull(Pageable pageable);

   Page<NetworkConnection> findByStreetName(String streetName, Pageable pageable);

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = """
                INSERT INTO network_connections(start_id, end_id, link_type, geom, external_id, osm_id)
                SELECT
                    pole.id,
                    bdp.building_id,
                    'DROP',
                    ST_MakeLine(pole.geom, bdp.geom),
                    'DROP_' || pole.external_id || '_' || bdp.building_id, -- bdp.external_id is not available,
                    bdp.building_id
                FROM building_drop_points bdp
                JOIN network_points pole ON pole.id = bdp.parent_id
            """)
   void insertDropConnections();

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = """
             INSERT INTO network_connections (start_id, end_id, link_type, geom, external_id)
             SELECT
                 cabinet.id,
                 pole.id,
                 'FEEDER',
                 COALESCE(
            		    ST_LineMerge(ST_Collect(ns.geom)),
            		    ST_MakeLine(cabinet.geom, pole.geom)
            		),
            		'FEEDER_' || cabinet.external_id || '_' || pole.external_id
             FROM network_points pole
             JOIN network_points cabinet ON cabinet.id = pole.parent_id
             JOIN LATERAL (
                 SELECT v.id AS source_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> cabinet.geom
                 LIMIT 1
             ) src ON true
             JOIN LATERAL (
                 SELECT v.id AS target_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> pole.geom
                 LIMIT 1
             ) tgt ON true
             LEFT JOIN LATERAL (
                 SELECT ns2.geom
                 FROM pgr_dijkstra(
                     'SELECT id, source, target, cost FROM noded_streets',
                     src.source_node,
                     tgt.target_node,
                     directed := false
                 ) path
                 JOIN noded_streets ns2 ON ns2.id = path.edge
                 WHERE path.edge != -1
             ) ns ON true
             WHERE pole.type = 'POLE'
             GROUP BY cabinet.id, pole.id, cabinet.geom, pole.geom
            """)
   void insertFeederConnections();

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = """
             INSERT INTO network_connections (start_id, end_id, link_type, geom, external_id)
             SELECT
                 aggregator.id,
                 cabinet.id,
                 'DISTRIBUTION',
                 COALESCE(
            		    ST_LineMerge(ST_Collect(ns.geom)),
            		    ST_MakeLine(aggregator.geom, cabinet.geom)
            		),
            		'DISTRIBUTION_' || aggregator.external_id || '_' || cabinet.external_id
             FROM network_points cabinet
             JOIN network_points aggregator ON aggregator.id = cabinet.parent_id
             JOIN LATERAL (
                 SELECT v.id AS source_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> aggregator.geom
                 LIMIT 1
             ) src ON true
             JOIN LATERAL (
                 SELECT v.id AS target_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> cabinet.geom
                 LIMIT 1
             ) tgt ON true
             LEFT JOIN LATERAL (
                 SELECT ns2.geom
                 FROM pgr_dijkstra(
                     'SELECT id, source, target, cost FROM noded_streets',
                     src.source_node,
                     tgt.target_node,
                     directed := false
                 ) path
                 JOIN noded_streets ns2 ON ns2.id = path.edge
                 WHERE path.edge != -1
             ) ns ON true
             WHERE cabinet.type = 'CABINET'
             GROUP BY aggregator.id, cabinet.id, aggregator.geom, cabinet.geom
            """)
   void insertDistributionConnections();

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = """
             INSERT INTO network_connections (start_id, end_id, link_type, geom, external_id)
             SELECT
                 exchange.id,
                 aggregator.id,
                 'TRUNK',
                 COALESCE(
            		    ST_LineMerge(ST_Collect(ns.geom)),
            			ST_MakeLine(exchange.geom, aggregator.geom)
            		),
            		'TRUNK_' || exchange.external_id || '_' || aggregator.external_id
             FROM network_points aggregator
             JOIN network_points exchange ON exchange.id = aggregator.parent_id
             JOIN LATERAL (
                 SELECT v.id AS source_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> exchange.geom
                 LIMIT 1
             ) src ON true
             JOIN LATERAL (
                 SELECT v.id AS target_node
                 FROM noded_streets_vertices_pgr v
                 ORDER BY v.the_geom <-> aggregator.geom
                 LIMIT 1
             ) tgt ON true
             LEFT JOIN LATERAL (
                 SELECT ns2.geom
                 FROM pgr_dijkstra(
                     'SELECT id, source, target, cost FROM noded_streets',
                     src.source_node,
                     tgt.target_node,
                     directed := false
                 ) path
                 JOIN noded_streets ns2 ON ns2.id = path.edge
                 WHERE path.edge != -1
             ) ns ON true
             WHERE aggregator.type = 'AGGREGATOR'
             GROUP BY exchange.id, aggregator.id, exchange.geom, aggregator.geom
            """)
   void insertTrunkConnections();


}
