# Planning Requirements

## Introduction

Align Aerospace http://......... is ???Description????

Align has two systems, U.S. Operations utilize SAP and the French Operations 
utilize Dymax, a VMS/COBOL/CODASYL/terminal based aerospace software package.

## Questions

#.  How do you transfer inventory between France and the US? Inter company sales?

#.  How do you value inventory?
    
    * Actual Landed cost
    * weighted average by part number/manufacturer
    * replacement cost

#. Purchasing
   
   #. How do you check to see if you have the existing inventory under a different part number?
   #. How do you check if the the part is available from the sister company?
   #. Do you track and trend vendor quotes?
   #. Do you check buy quantities against a projected per unit cost by extrapolating cost per unit at 
different quantities?
   #. What is the process for obtaining vendor quotes?  e.g.
      #. The system evaluates future needs and generates a request for quote, which is 
         #. Send it XML or JSON to an API to the vendor
         #. Sent to the vendor using EDI X.12
         #. Faxed to the vendor
         #. Emailed to the vendor
      #. How is the response recorded?
   
#. Sales
   
   #. How do you monitor customer quote conversions?
 

## Objectives

#. Create a global data repository to facilitate inventory planning and purchasing.

#. Create data to be processed in SAP including

* Purchase orders

* Customer Orders

* Facility Transfers

## Data Requirements

### Supply

#### Multiple Certifications

#. Does the system support certification of inventory to more than one part number?

#. How is inventory reserved for certain customers, e.g. JIT/LTA/SLA/kits

### Demand

#. Does the system support various levels of planning, each with its own:
   #. Sales History
   #. Inventory Availability Rules
   #. Forecast
   #. Location / Customer Set level

#. Does the system support certifications for parts for example the following at the lot level

* MFGCOFC  Manufacturer Certificate Of Conformance

* MILSPEC  MIL SPEC DOCUMENTATION

* PHYTEST  PHYSICAL TEST REPORTS

* PLATCERT PLATING CERTIFICATE

* D590BAC  D590 Parts

* 8130/MCC BOEING 8130 OR MCC

* CLASS3IN CLASS3 INSPECTION REQUIRED

* DSQAR    Delegated Supplier QA Representative

* FAA8130  FAA8130

* CPT      Certificate Of Proof Test

## Data Export

Can data be exported to comply with a JSON schema?

Can REST services be invoked on the resulting data?

### Example Item Master  JSON

```
{
  "icItemMasts": [
    {
      "itemNbr": 33791,
      "itemCd": "BACB30VT6K3",
      "itemDescr": "PIN",
      "stkUm": "EA",
      "sellUm": "EA",
      "stdCost": 1.163108,
      "icCategoryNbr": 250,
      "nonStkFlg": "N",
      "serNbrReqrFlg": "N",
      "mfrSerNbrReqrFlg": "N",
      "inspectReqrFlg": "Y",
      "mfrLotCtrlFlg": "Y",
      "kitFlg": "N",
      "priceChgFlg": "Y",
      "sellFlg": "Y",
      "harmonizedCd": "73182990009967",
      "planBucketSz": 2,
      "currCd": "CAD",
      "reqrMfrFlg": "Y",
      "leadTmDy": 630,
      "statId": "A",
      "pickScanId": "L",
      "introDt": "2012-09-15T04:33:59+07",
      "splitAtBinFlg": "N",
      "fifoDtId": "R",
      "replenTypeId": "B",
      "icItemRestrictCusts": []
    },
    {
      "itemNbr": 53515,
      "itemCd": "HST10AG6-3",
      "itemDescr": "PIN",
      "stkUm": "EA",
      "sellUm": "EA",
      "stdCost": 0.524488,
      "icCategoryNbr": 250,
      "nonStkFlg": "N",
      "serNbrReqrFlg": "N",
      "mfrSerNbrReqrFlg": "N",
      "inspectReqrFlg": "Y",
      "mfrLotCtrlFlg": "Y",
      "kitFlg": "N",
      "priceChgFlg": "Y",
      "sellFlg": "Y",
      "harmonizedCd": "73182990009967",
      "planBucketSz": 2,
      "currCd": "CAD",
      "reqrMfrFlg": "Y",
      "leadTmDy": 700,
      "statId": "A",
      "pickScanId": "L",
      "introDt": "2006-10-10T04:33:59+07",
      "splitAtBinFlg": "N",
      "fifoDtId": "R",
      "replenTypeId": "B",
      "icItemRestrictCusts": []
    },
    {
      "itemNbr": 127544,
      "itemCd": "B0206001AG6-3",
      "itemDescr": "HI LITE",
      "stkUm": "EA",
      "sellUm": "EA",
      "icCategoryNbr": 254,
      "nonStkFlg": "N",
      "serNbrReqrFlg": "N",
      "mfrSerNbrReqrFlg": "N",
      "inspectReqrFlg": "Y",
      "mfrLotCtrlFlg": "Y",
      "kitFlg": "N",
      "priceChgFlg": "Y",
      "sellFlg": "Y",
      "harmonizedCd": "88033000009967",
      "planBucketSz": 2,
      "currCd": "CAD",
      "reqrMfrFlg": "Y",
      "leadTmDy": 142,
      "statId": "A",
      "pickScanId": "L",
      "introDt": "2011-12-19T04:33:59+07",
      "splitAtBinFlg": "N",
      "fifoDtId": "R",
      "replenTypeId": "B",
      "icItemRestrictCusts": []
    },
    {
      "itemNbr": 346096,
      "itemCd": "HST10AG-6-3",
      "itemDescr": "Use HST10AG6-3",
      "stkUm": "EA",
      "sellUm": "EA",
      "icCategoryNbr": 636,
      "nonStkFlg": "N",
      "serNbrReqrFlg": "N",
      "mfrSerNbrReqrFlg": "N",
      "inspectReqrFlg": "Y",
      "mfrLotCtrlFlg": "Y",
      "kitFlg": "N",
      "priceChgFlg": "Y",
      "harmonizedCd": "73181590459967",
      "reqrMfrFlg": "Y",
      "leadTmDy": 700,
      "statId": "A",
      "pickScanId": "L",
      "introDt": "2015-11-01T13:37:38+07",
      "splitAtBinFlg": "N",
      "fifoDtId": "R",
      "replenTypeId": "D",
      "icItemRestrictCusts": []
    },
    {
      "itemNbr": 1304665,
      "itemCd": "100-159-6-3",
      "itemDescr": "PIN",
      "stkUm": "EA",
      "sellUm": "EA",
      "icCategoryNbr": 250,
      "nonStkFlg": "Y",
      "serNbrReqrFlg": "N",
      "mfrSerNbrReqrFlg": "N",
      "inspectReqrFlg": "Y",
      "mfrLotCtrlFlg": "Y",
      "kitFlg": "N",
      "priceChgFlg": "Y",
      "sellFlg": "Y",
      "harmonizedCd": "8803300000",
      "reqrMfrFlg": "Y",
      "leadTmDy": 114,
      "statId": "A",
      "pickScanId": "L",
      "introDt": "2018-12-15T04:33:59+07",
      "splitAtBinFlg": "N",
      "fifoDtId": "R",
      "replenTypeId": "B",
      "icItemRestrictCusts": []
    }
  ]
}
```

