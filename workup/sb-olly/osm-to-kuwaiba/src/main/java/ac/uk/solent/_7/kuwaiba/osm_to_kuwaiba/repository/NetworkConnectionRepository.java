package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.LinkType;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models.NetworkConnection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NetworkConnectionRepository extends JpaRepository<NetworkConnection, Long> {

	// These Retrievers are to be used by the rest api
	List<NetworkConnection> findByLinkType(LinkType linkType);
	
	List<NetworkConnection> findByStartId(Long startId);
	
	List<NetworkConnection> findByEndId(Long endId);
	
	List<NetworkConnection> findByStartIdOrEndId(Long startId, Long endId);
	
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_connections(start_id, end_id, link_type, geom)
        SELECT 
            pole.id,
            bdp.building_id,
            'DROP',
            ST_MakeLine(pole.geom, bdp.geom)
        FROM building_drop_points bdp
        JOIN network_points pole ON pole.id = bdp.parent_id
    """)
    void insertDropConnections();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_connections (start_id, end_id, link_type, geom)
        SELECT
            cabinet.id,
            pole.id,
            'FEEDER',
            ST_MakeLine(cabinet.geom, pole.geom)
        FROM network_points pole
        JOIN network_points cabinet ON cabinet.id = pole.parent_id
        WHERE pole.type = 'POLE'
    """)
    void insertFeederConnections();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_connections (start_id, end_id, link_type, geom)
        SELECT
            aggregator.id,
            cabinet.id,
            'DISTRIBUTION',
            ST_MakeLine(aggregator.geom, cabinet.geom)
        FROM network_points cabinet
        JOIN network_points aggregator ON aggregator.id = cabinet.parent_id
        WHERE cabinet.type = 'CABINET'
    """)
    void insertDistributionConnections();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO network_connections (start_id, end_id, link_type, geom)
        SELECT
            exchange.id,
            aggregator.id,
            'TRUK',
            ST_MakeLine(exchange.geom, aggregator.geom)
        FROM network_points aggregator
        JOIN network_points exchange ON exchange.id = aggregator.parent_id
        WHERE aggregator.type = 'AGGREGATOR'
    """)
    void insertTrunkConnections();
}
