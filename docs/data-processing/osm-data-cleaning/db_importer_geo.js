const fs = require('fs');
const parseOSM = require('osm-pbf-parser');

const inputPath = 'hampshire-260224.osm.pbf';
const outputPath = 'hampshire-telecom.geojson';

const parser = parseOSM();
const outputStream = fs.createWriteStream(outputPath);

outputStream.write('{\n"type": "FeatureCollection",\n"features": [\n');
let firstItem = true;

fs.createReadStream(inputPath)
    .pipe(parser)
    .on('data', (items) => {
        items.forEach(item => {
            // Filter logic for Telecom, Houses, and Streets
            const isTelecom = item.tags && (item.tags.telecom || item.tags.utility === 'pole');
            const isHouse = item.tags && item.tags.building;

            if (item.type === 'node' && (isTelecom || isHouse)) {
                // 2. Format as a GeoJSON 
                const feature = {
                    type: "Feature",
                    properties: {
                        id: item.id,
                        category: isTelecom ? 'infrastructure' : 'house',
                        ...item.tags
                    },
                    geometry: {
                        type: "Point",
                        coordinates: [item.lon, item.lat]
                    }
                };

                if (!firstItem) outputStream.write(',\n');
                outputStream.write(JSON.stringify(feature));
                firstItem = false;
            }
        });
    })
    .on('end', () => {
        outputStream.write('\n]\n}');
        outputStream.end();
        console.log('Success! GeoJSON ready for QGIS:', outputPath);
    });