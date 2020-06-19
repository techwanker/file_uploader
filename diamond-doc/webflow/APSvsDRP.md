# Diamond APS vs Traditional DRP

"Aerospace distribution can be very profitable if the buying is correct".

* Buy the right parts

* Buy the optimal quantity

## Introduction 

Define a process to 

#. Improve purchasing efficiency
#. Provide decision support for better purchasing decisions
#. Monitor performance
#. Create and enforce policies
#. Improve detailed and summary information
#. Leverage inter company inventory 

## Objectives

Lower unit cost

We will introduce:

* Multiple Certifications

* Supply Prioritization

* Eligible Supply


## Traditional DRP

### Forecast

* History is aggregated by month
* Various forecast models are applied to the history simulating forecasts into historical periods
* Forecast performance is evaluated over lead time 
* Best performing model is chosen
* Forecast is made
* Safety stock is calculated based on service level and forecast statistics
* Economic Order Quantity is computed

### Projections

*Buckets* are created, usually monthly

Starting with onhand inventory

Forecast demand is decrease by firm demand during the corresponinding bucket

For each sucessive bucket the preceeeding position is incremented by replensishments and decremented by
unconsumed forecast and actual demand.

Additional Replenishment Orders are created based on the EOQ and incremental purchase quantities.

### SKUs

In traditional DRP a Stock Keeping Unit is the basis of planning.

Say there are four different *D Cell batteries*, 888888-333333 F-R-Eddy Alkaline and 666666-22222
Deer-a-Bull and 33333-55555 BRAND-X 77777-22222 BRAND-Z.  

These batteries would be planned independently.

Now consider an engineer for a major flashlight compant specifies 
that specifies a battery based on dimensions, chemical composition and electrical
properties, voltage and ampere hours and a voltage discharge curve and calls this *BATT-D-ALK*.

F-R-Eddy, Deer-a-Bull and Brand-X all issue a *Certificate of Compliance* stating that 
their batteries meet all of the engineering requirement of *BATT-D-ALK*

Let''s assume

1,000  888888-333333 

2,000  666666-222222

500   333333-555555

300   777777-222222

How many *BATT-D-ALKS* do you have?

Answer: You have 3,500

## Scenario

Now assume you get a customer order for 1,000 *BATT-D-ALK* for next month.

What is your projected position for each of the parts at the end of next month?

That depends on whether the 888888-333333, the 66666-222222 or the 333333-555555 parts are used.

Perhaps:

### Supply Prioritization

This section introduces the concept of supply prioritization, determinining which eligible supply to 
to use to satisfy demand.

* 88888-33333 Parts cost more so you fill with the cheaper part
* You use some of each because they have a shelf life and you want to get rid of those near the expiry date.
* Another customer will only accept from Deer-A-Bull so you fulfill with F-R-Ready to reserve your Deer-A-Bull 
inventory as it is more widely accepted.


Perhaps:
The customer only approves

## Buy Quantity

Simple part, no equivalent parts.

Average monthly consumption is 200 units

Vendor quote 
unit price 1,000 3.02
           2,500 1.647

So you buy a years supply and move onto the next part.

STOP!

Let''s assume unit_cost = setup_cost/nbr_units + incremental cost

substituting in *unit_cost* and *nbr_units* in the equation twice leaves us 
two equations with two unknowns.  Using linear algebra we solve for
the setup cost and incremental_cost and plot this.

We see  ... 

Now we can quote lower prices and hopefully get a higher quote conversion while
simultaneously getting a higher profit margin.

???



### Prioritization

####  Sourcing Rule

#### Purchase Orders 

##### Availability Date

##### Late 

##### Vendor On Time Performance

#### Attribute Weight values

#### Other Demand for 

#### FIFO
 
# Data Requirements

### Supply 

Onhand Inventory
Purchase Orders 
Work Orders

### Demand 

Forecast
Customer Orders
Work Orders

### Other
Item Master


History
Vendor Quotes
Customer Quotes
Approved Manufacturers

# Legacy System 

No changes to the legacy system will be required

Diamond APS can support

Vendor Quotes
Customer Quotes
Requisitions

## Legacy System

Purchasing interaction with Dymax and SAP can be reduced to a data entry function, all decisions 
can be made in Diamond.

## Risk

There is zero risk, no existing systems are modified.  No execution processes are affected.

# Concepts

## Supply Pools

Supply pools are logical collections of inventory and may be used to 

#. Ensure that the pool is only available to the appropriate customers
#. Distinguish between regular, buyback and consigment inventory
#. Used in prioritization of supply for demand

## Forecast Groups

Demand for a customer or a collection of customers may be aggregated in history and an aggregate forecast created.
Forecast groups may have their own eligibility constraints.

## Eligible Inventory

Aerospace parts are not fungible, more than a specified part number is necessary to satify customer demand.

## Demand Prioritization

Demand prioritization is the set of rules that determines which demands get fulfilled and in what order.

## Supply Prioritization

Supply prioritization is the set of rules that determines which inventory is consumed for a given demand.

## Forecast Consumption

# Procedures

## Requisitions

## Review

## Approval

## Purchasing Approval

## Portal


# Benefits

# Questions

#. How long does it take to get

Sales History
Vendor Quotes
Customer Quotes
Approved Manufacturers
Item Equivalencies
Forecasted Demand
Purchase Orders
Requisitions

For a part and all of its equivalents in both the United States and France?

#. How do you compute a buy quantity?
