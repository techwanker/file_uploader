Questions
=========

#. How many buyers are there?

#. How many purchase orders are created per year?

#. How much time does it take to create a purchase order?

#. Gather the information

   -  Onhand Inventory

   -  Sales History

   -  Sales Forecast

   -  Forecast Consumption

   -  Approved Manufacturers

   -  Vendor Quotes

   -  Existing Purchase Orders

   #. Vendor Quotes

   -  Get quotes for the parts

   -  Compute Optimal Replenishment Quantity (Not Economic Order
      Quantity)

   -  Create a requisition

#. What are the requisition review requirements?

   -  When is a requisition subject to review?

   -  What are the needs additional work conditions?

      -  Need more vendor quotes

      -  Need work on equivalent parts

      -  Incorrect buy quantity

      -  Get existing inventory certified.

Out of Scope
------------


Lead times can vary drastically based on

-  vendor

-  material shortages

-  new product introduction

-  product recall MRO (Maintenance, Repair and Overhaul)Definition

The elapsed time, usually measured in weeks between when a product
is ordered and receivied.

Usually not considered

-  Recieving time

-  Inspection Time

-  Intra-facility transfer time

-  These are consider *availability times*

Typically in DRP Planning

-  There is only one lead time

-  This lead time is fairly consistent

-  Costs due not vary based on lead time.

In aerospace
------------

Source of Lead Time
-------------------

vendor quotes
~~~~~~~~~~~~~

Take the lead time from the maximum quote expiration date

Summarized Lead Times should include
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Vendor Code

-  Vendor type (Manufacturer, Distibutor, intra-company

-  Vendor quote beginning and ending effective date

-  Date of request for quote

Lead time details should include:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Historical lead times

Lead time projections
~~~~~~~~~~~~~~~~~~~~~

Factors that can effect lead time, Lead times can vary drastically based
on

-  vendor

-  Some vendors will stock

-  Some manufacturers will build to stock

-  Some manufactureres will build only on demand, see Cost per Unit

-  material shortages

-  new product introduction

-  product recall MRO (Maintenance, Repair and Overhaul)

| 



Instructions
------------

#. Look at the report section of the portal

#. Review the information

#. Fill out the checklist

#. Create the requisition

Requisition Checklist
~~~~~~~~~~~~~~~~~~~~~

Sequence Diagram
----------------

.. image::  ./images/Requisitions.png

Intrinsic Information
~~~~~~~~~~~~~~~~~~~~~

Extrinsic Information
~~~~~~~~~~~~~~~~~~~~~~

Gather all information, it should all be available in the portal

Related articles
----------------


Service Level Agreements
========================


Definitions
===========

Definitions:: 

    ABC_SLS_PCT_DLR 

        Top 20 of previous 12 month contribution to sales dollars
        Does not adequately support new product introduction

    ABC_CUST_QTE

    SVC_LVL

        Contractual service levels

    ABC_SLS_PCT ABC Sales Percent
