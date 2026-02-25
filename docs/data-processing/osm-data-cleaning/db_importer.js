const fs = require('fs');
const parseOSM = require('osm-pbf-parser');

const inputPath = 'hampshire-260224.osm.pbf';
const outputPath = 'hampshire-260224.json';

const parser = parseOSM();
const outputStream = fs.createWriteStream(outputPath);

outputStream.write('[\n');
let firstItem = true;

console.log('Extracting relevant data');

fs.createReadStream(inputPath)
    .pipe(parser)
    .on('data', (items) => {
        items.forEach(item => {
            let relevant = false;
            let preparedItem = {
                id: item.id,
                type: item.type,
                tags: item.tags || {}
            };

            // Filter for Streets 
            if (item.type === 'way' && item.tags && item.tags.highway) {
                preparedItem.category = 'street';
                preparedItem.refs = item.refs;
                relevant = true;
            }

            // Filter for Buildings
            else if (item.tags && item.tags.building) {
                preparedItem.category = 'house';
                preparedItem.address = {
                    number: item.tags['addr:housenumber'],
                    street: item.tags['addr:street']
                };
                relevant = true;
            }

            // Filter for Artifacts
            else if (item.tags && (item.tags.telecom || item.tags.utility === 'pole' || item.tags.manmade === 'manhole')) {
                preparedItem.category = 'infrastructure';
                preparedItem.lat = item.lat;
                preparedItem.lon = item.lon;
                relevant = true;
            }

            if (relevant) {
                if (!firstItem) outputStream.write(',\n');
                outputStream.write(JSON.stringify(preparedItem));
                firstItem = false;
            }
        });
    })
    .on('end', () => {
        outputStream.write('\n]');
        outputStream.end();
        console.log('Data saved to:', outputPath);
    });