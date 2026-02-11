Two main options are avaliable but for now we will stick with option 1
# Option 1: Overpass Turbo [https://overpass-turbo.eu/]
Overpass does exactly what we need, but it comes with a few downsides which we can mitigate.  
We may have to space out queries with a script which Craig has mentioned already (we can either use his or create our own). Also overpass is gonna struggle when it comes to huge areas (large cities or bigger) but i believe this is the best choice for our project.  
If our queries are too strong for overpass turbo we can download the regular overpass api software  
Overpass info: [https://wiki.openstreetmap.org/wiki/Overpass_API]   
We can use a feature called "bounding box" to create a square area we want to query <br>
We need to export the data as JSON or a CSV.

# Option 2: Geofabrik [https://download.geofabrik.de]
Going down the path of geofabrik means downloading entire continets worth of Open Street Map (OSM) data. This would require us to have our own database to store this data and pull from it with every query.  
This adds an extra layer of complexity to the project and would only be worth it if we set this project up as business with hundreds of users.
