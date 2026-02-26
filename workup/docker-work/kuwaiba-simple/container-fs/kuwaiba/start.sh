#!/bin/sh

# -------------------------------------------------------------
# startup script for kuwaiba which copies data in from overlay
# -------------------------------------------------------------

set -e # exit script on error
set +x # do not print out lines as executed

echo "external start script for kuwaiba"

# Error codes
E_ILLEGAL_ARGS=126
E_INIT_CONFIG=127

export KUWAIBA_DATA_ARCHIVE="/data-archive"
export KUWAIBA_DATA_ZIP="/data-zip"
export KUWAIBA_OVERLAY="/data-overlay"
export KUWAIBA_DATA="/data"
TMP_ZIP=/tmp

applyOverlayConfig() {

  # check if database installed in volume. If installed do not unzip and replace database 
  if [ ! -f "${KUWAIBA_DATA}/configured" ]; then
    echo "Only default Kuwaiba database installed in volume. Checking whether to unzip database."
    if [ -f "${KUWAIBA_DATA_ZIP}/data.zip" ]; then
    
       archivefilename=data_$(date +%Y%m%d_%H%M%S)
       
       echo "Reinstalling database and data in volume from provided zip file. Unzipping from ${KUWAIBA_DATA_ZIP}/data.zip to ${KUWAIBA_DATA}"
    
       echo "copying old data ${KUWAIBA_DATA} to local archive ${KUWAIBA_DATA_ARCHIVE}/${archivefilename}"
       cp -r  "${KUWAIBA_DATA}" "${KUWAIBA_DATA_ARCHIVE}/${archivefilename}"
       
       echo "deleting old ${KUWAIBA_DATA}"
       rm -rf "${KUWAIBA_DATA}/*"
       mkdir -p ${KUWAIBA_DATA}/logs/scheduling 
       mkdir -p ${KUWAIBA_DATA}/logs/kuwaiba 
       mkdir -p ${KUWAIBA_DATA}/logs/sync 
       
       echo "deleting old ${TMP_ZIP}/kuwaiba"
       rm -rf ${TMP_ZIP}/kuwaiba
       mkdir ${TMP_ZIP}/kuwaiba
       
       echo "unzipping ${KUWAIBA_DATA_ZIP}/data.zip to temporary folder "
       unzip ${KUWAIBA_DATA_ZIP}/data.zip -d ${TMP_ZIP}/kuwaiba
       
       echo "copying config to ${KUWAIBA_DATA}"
       cp -r --verbose ${TMP_ZIP}/kuwaiba/data/* ${KUWAIBA_DATA}

       chown -R kuwaiba:kuwaiba ${KUWAIBA_DATA} 
       
       echo "base database and files copy from zip completed"
    else
       echo "No data zip found at ${KUWAIBA_DATA_ZIP}/data.zip Using default configuration from docker image"
    fi

    install /dev/null "${KUWAIBA_DATA}/configured"

  else
    echo "Database already configured in volume. Using existing database"
  fi

  # Overlay relative to the root of the install dir
  if [ -d "${KUWAIBA_OVERLAY}" ] && [ -n "$(ls -A ${KUWAIBA_OVERLAY})" ]; then
    echo "Apply custom configuration from ${KUWAIBA_OVERLAY}."
    # Use rsync so that we can overlay files into directories that are symlinked
    rsync -K -rl --out-format="%n %C" ${KUWAIBA_OVERLAY}/* ${KUWAIBA_DATA}/. || exit ${E_INIT_CONFIG}
  else
    echo "No custom config found in ${KUWAIBA_OVERLAY}. Using existing configuration in volume."
  fi
}

# main program section

echo "Testing if database has been created and applying overlay configuration"

applyOverlayConfig

echo "starting kuwiba"

java -jar /opt/programs/kuwaiba_server_2.1.1-stable.jar 2>&1 | tee /data/logs/kuwaiba_$(date +%Y%m%d_%H%M%S).log


