gulp build
docker build -t dataprep/web:latest .
docker tag --force dataprep/web:latest tal-qa158.talend.lan:5000/dataprep/web:latest
docker push tal-qa158.talend.lan:5000/dataprep/web:latest

