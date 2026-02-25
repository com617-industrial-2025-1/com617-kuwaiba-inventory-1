const data = require('./hampshire-260224.json');

const audit = {
    streets: 0,
    houses: 0,
    infrastructure: 0,
    missingAddress: 0
};

data.forEach(item => {
    if (item.category === 'street') audit.streets++;
    if (item.category === 'house') {
        audit.houses++;
        if (!item.address.number) audit.missingAddress++;
    }
    if (item.category === 'infrastructure') audit.infrastructure++;
});

console.log('--- Data Audit Results ---');
console.log(`Streets Found: ${audit.streets}`);
console.log(`Houses Found: ${audit.houses} (Missing Addresses: ${audit.missingAddress})`);
console.log(`Telecom Artifacts Found: ${audit.infrastructure}`);
