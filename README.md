# mod-acquisitions

This software is distributed under the terms of the Apache License, Version 2.0. See the file ["LICENSE"](https://github.com/folio-org/raml-module-builder/blob/master/LICENSE) for more information.


#### Demo acquisitions module exposing some acquisitions apis and objects based on the raml-module-builder framework against a MongoDB

This project is built using the raml-module-builder, using the MongoDB async client to implement some basic acquisition APIs. The project also includes a small demo of the drools functionality. It is highly recommended to read the [raml-module-builder README](https://github.com/folio-org/mod-acquisitions/blob/master/README.md)

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

clone / download the raml-module-builder and `mvn clean install`

then do the same for the current project `mvn clean install`

Run:

java -jar acquisitions-fat.jar -Dhttp.port=8083 embed_mongo=true


Or via dockerfile

note that the embedded mongo is started on a dynamic port chosen at embedded mongo start up - refer to the log ("created embedded mongo config on port 54851")


#### Querying examples:

```sh
make sure to include appropriate headers as the runtime framework validates them

Authorization: aaaaa
Accept: application/json


simple query

http://localhost:8083/apis/invoices

query with a filter (mongo syntax)

http://localhost:8083/apis/invoices?query={"$and": [ { "total_amount": { "$lt": 111 } }, { "vendor_contact_person.first_name": "joe" } ]}
```

#### (see online documentation for additional options)

http://localhost:8083/apidocs/index.html?raml=raml/acquisitions/acquisitions.raml

#### Post example:

```sh

http://localhost:8083/apis/invoices

make sure to include appropriate headers as the runtime framework validates them

Authorization: aaaaa
Accept: application/json
Content-Type: application/json


{
  "vendor_invoice_number": "1234567890",
  "invoice_date": "1975-03-30",
  "total_amount": 100,
  "currency": {
    "value": "USD",
    "desc": "US Dollar"
  },
  "vendor_code": {
    "value": "AutoEDI_MainCode",
    "desc": "AutoEDI_Name"
  },
  "vendor_contact_person": {
    "first_name": "joe",
    "last_name": "z"
  },
  "payment": {
    "prepaid": false,
    "tracking_purpose_only": false,
    "send_to_erp": true,
    "payment_status": "NOT_PAID",
    "date_of_payment": "1975-03-30",
    "final_amount_paid": 0,
    "payment_currency": {
      "value": "USD",
      "desc": "US Dollar"
    }
  },
  "payment_method": {
    "value": "ACCOUNTINGDEPARTMENT",
    "desc": "Accounting Department"
  },
  "created_from": {
    "value": "EDI",
    "desc": "EDIteur Invoice Message"
  },
  "invoice_status": {
    "value": "ACTIVE",
    "desc": "Active"
  },
  "approved_by": "You",
  "additional_charges": {
    "shipment": 0,
    "overhead": 0,
    "insurance": 0,
    "discount": 0,
    "use_pro_rata": false
  },
  "invoice_vat": {
    "taxable": false,
    "vat_percent": 0,
    "type": {
      "value": "INCLUSIVE",
      "desc": "Inclusive"
    },
    "vat_amount": 0,
    "vat_expended_from_fund": true
  },
  "note": "abc",
  "invoice_line": [
    "12356346546",
    "12356346547",
    "12356346548"
  ]
}

```
