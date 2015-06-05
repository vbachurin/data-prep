# Talend Data Preparation - Platform
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains the REST service to all Transformation operations.

## Dockerfile

this dockerfile is designed to build the 'data' image based on what's locally in 'runtime' folder.
#### 1. use 'runtime' folder as a volume for your images like:
```
mongodb:
    volumes:
        - runtime/mongodb:/data/db
transform:
    volumes:
        - runtime/dataset/store:/store
dataset:
    volumes:
        - runtime/dataset/store:/store
```
#### 2. building the image will COPY this folder to the image.
```
docker build -rm=true -t talend/dataprep-data:<image_version> .
```
#### 3. tag & push to talend-registry
```
docker tag talend/dataprep-data:<image_version> talend-registry:5000/talend/dataprep-data:<image_version>
docker push talend-registry:5000/talend/dataprep-data:<image_version>
```

#### 4. use your new image
Then use the 'data' image in fig files instead of the volumes.
like:
```
data:
    image: talend/dataprep-data:1.0.m0
    volumes:
        - /data/db
        - /store
mongodb:
    volumes_from:
        - data
transform:
    volumes_from:
        - data
```

## Fig option for accesing the Talend registry   
you may use the option `--allow-insecure-ssl` when doing a **fig up** to allow fig to acces the Talend registry without errors.

## License

Copyright (c) 2006-2015 Talend
