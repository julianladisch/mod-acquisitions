# acquisitions
Demo acquisitions module exposing acq apis and objects based on the raml-module-builder framework against MongoDB

This project is built using the raml-module-builder, using the MongoDB async client to implement some basic acquisition APIs. The project also includes a small demo of the drools functionality.

APIs Implemented:

    Funds CRUD
    Invoices CRUD

Objects / Schemas:

    Funds
    Invoices
    Invoice Lines
    Purchase Order Lines
    Vendor

Can be run in both embedded mongodb mode or with a regular MongoDB server

instructions:

mvn clean install

Run:

java -jar acquisitions-fat.jar -Dhttp.port=8082 embed_mongo=true
