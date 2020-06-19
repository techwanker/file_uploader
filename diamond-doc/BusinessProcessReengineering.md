# Business Process Reeingeneering


## Objectives

#. Maximum buyer productivity
#. Eliminate unnecessary purchases
#. Develop a standardized methodology for buyers that is deterministic, with the same input two buyers should come to the same conclusions.
#. Buyers should be able to test the results of a simulation by, for example adding a secondary manufacturer CofC to a lot and replan the part and get the answer back within a second with a new single screen.
#. All scenarios should be stored with the results.
#. Upon acceptance of a scenario the simulation changes should be reported so 
that the source systems can be update.
#. Status of simulation changes
   # Requested
   # Not possible - stops further recommendations to take this action
   # Active - Once a download from the source system reflects this change
#. A report of requested modification not yet completed on source systems
   


## Purchasing Operational Efficiency

### Purchasing Review Board
   Requisitions may be reviewd by the purchasing review board
   * Approval
   * Disapproval  
     * Record disapproval reason for requisitions
       Purchasing review board can select (or create and select) a reason such
as   
       * Review equivalent parts
       * Is onhand under another part
       * insufficient quotations (other vendors may have lower costs)

### Speed up quotations
   * Automatically email vendors request for quotations 
   * Automated quote response
     have the vendors provide a CSV, JSON or XML file with the quote to be
   * automatically uploaded to the system.
   
   For example a vendor could create a spreadsheet with the following columns

   * item_cd
   * quantity
   * manufacturer
   * price 
   * available date
  
   by emailing to quotes@yourco.com these quotes can be automatically loaded
into the system without changes to the legacy system,     

### Buyer information

The buyer should have single screen that shows:

#. Supplies
  #. On hand
  #. Open Purchase Orders
  #. Open Work Orders
#. Demand 
  #. Forecasted
    #. Raw
    #. Consumed
    #. Unconsumed
  #. Safety Stock
#. Reserved Inventory
  #. Quarantined
  #. Restricted access (JIT programs, Committed Service Level Agreement Plans)
#. All part numbers in the planning group  
  #. Every part and all equivalents, transitively, that is the equivalents to those equivalents until exhausted.
  #. Customer specific substitutions
#. Approved manufacturer matrix
  Customers down the left, manufacturers across the top 
#. Requisitions
#. Supplier on-time historical metrics
#. Supply ineligibility drill-down
#. Vendor Quotes
#. Time phased inventory position, Pipeline (Global, by Facility, by planner)
#. On hand inventory in aggregate with the ability to open details with a single click
#. Sales history for the last three years in multiple dimensions
  #. Time Dimensions include annual, quarterly and monthly
  #. Ability to see by customer 
#. Existing purchase orders
#. Existing facility transfers in process
#. Detailed reason why supplies are not eligible for a demand that is allocated late or short 
#. A matrix of approved manufactures and customers
#. See the part and all transitive equivalent parts
#. Late or short demands

In Diamond this is all done locally in the web browser with no network requests so it is virtually instaneous.

### Recommendations
#. Purchase orders that can be cancelled
#. Get a manufacturer Certificate of Compliance for existing inventory to satisfy a 
requisition with existing inventory
#. Supply prioritization
   Use buyback inventory before using our inventory for appropriate customers
#. Allocation based pricing
#. Items with a shelf life have oldest allocated first
#. Less valuable items are allocated first
   #. Based on Certifications (dual certed parts have more value)
#. Facility Transfer
#. Supply Pool Transfer
#. Expedite or de-expedite a purchase order

### Alerts
  * Obsolete Inventory
  * Expiring Inventory
  * Purchase Exceeding x% of previous maximum unit price
  * Purchase Exceeding x% of previous minimum unit price
  * Purchase of specified dollars not yet approved

Computing based on Setup
-------------------------

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


   
## Extensibility

   Any component must be easily plugged in with an alternative implementation
   that is compliant with the corresponding interface,

   * Demand Priority
   * Eligibility Requirements
   * Supply Prioritization
     * Lot value determination 
   * Recommendation Handlers for propagating accepted recommendations to source system
   

# Implementation
  
  * Extract necessary data from legacy systems
  * Load into Advanced Planning
  * Augment with necessary but unavailable information 
  * Run a full plan
  * Review recommendations
    * Accept recommendation (must define Action Handlers)
    Reject recommendation (select reason to be persisted across full reloads)

     
## Questions

#. Inventory Restriction 
   #. How do you restrict availability of inventory for special purposes such as
      * JIT contracts
      * Committed Service Level Agreements
      * Kitting and Assembly
   #. Do you have automated approved manufacturer eligibility?
   #. Do you have prioritization for lots with expiry dates?
   #. How do you calculate the residual cost of goods for broker buys for the
   #. Are you exclusively FIFO or do you consider lots that have lower cost that
satisfies the demand (taking into consideration multiple
certifications, incremental cost of Quality Assurance testing and destructive
tests?), etc.?
quantity that exceeds the customer demand?
#.  Quality Assurance
   Do you have a quality assurance program that supports skip lot testing and 
pre-approved lots ( lots that have already passed the QA requirements for a customer should 
receive higher priority for that customer and lower priority 
for others)    
#. What supply eligibility rules do you have?
#. How do you pin an allocation to a demand ?
#. Does your system recommend when alternate availability is preferable to a pinned allocation?



# SAP on the web

* https://www.brightworkresearch.com/sap/2017/11/best-understand-saps-negative-innovation/

* https://boards.straightdope.com/sdmb/archive/index.php/t-509111.html

* https://www.linkedin.com/pulse/who-knew-sap-could-so-complicated-heather-peyton/
* "We are nowhere near best-in-class, but we are making progress," says Steve Rogers, UK managing director of SAP to an audience of his customers at the German applications firm's annual user gathering at the end of last year. It's not the kind of comment that you expect from a senior executive at a leading software firm. 

* https://www.thirdstage-consulting.com/lessons-from-an-sap-failure-at-lidl/

* https://www.360cloudsolutions.com/top-six-erp-implementation-failures/

* https://www.brightworkresearch.com/saphana/2017/06/22/hana-big-data-equals-big-failure/

There is a huge amount that can be done but a specification has not been articulated.

Concepts must be articulated.

## Simple Example

During one of my calls with Peter he told me that he was reviewing purchase orders a simple line such as 
"Buyers don''t buy the correct quantities to get a good price" was extended to:

### Compute Optimal Purchase Quantity

Compute a projected per unit cost by solving the equation

unit_cost = (setup_cost / qty) + incremental cost

For two different known qty and prices (vendor quotes) using linear algebra

### Graph this relationsihip

Find the "price knee" the first derivative of the function, the slope of the tangent 
starts to level off (it asymptotically approaches 0, meaning the limit is the unit cost
doesn''t decrease at all. Depending on setup cost, incremental cost and annual consumption 
a three year supply may be ten percent more than a one year supply, it may also be 
three times the acquisition cost and additional carrying costs must be considered.

Vendor quotes should include this range of quantities, purchasing quantities should
be in this range, buys can be made and even scheduled so that lower per unit costs
can be realized.

## Purchasing Procedures

When a part needs to be replenished

#. Vendor quotes for the price range should be required.
#. Purchase amounts over a defined limit should be reviewed and approved.
#. Requisitions should be created in the new purchase decision application and once approved, be 
created as purchase orders in the execution system (Dymax and SAP).
#. Checks for any constraints including approved manufacturers should be simulated
#. Existing inventory carried under equivalent part numbers should be considered.

The opportunities for process improvement are best addressed by evaluating your current processes
and the issues your experts realize and developing a system to address those issues.

## Constraints

Your new process should:

#. Be external to SAP and Dymax, requiring no modifications to either system.  This eliminates risk and
complexity.
#. Should include data from both operations for inventory, purchases and demands
#. Incorporate new procedures and policies to reflect best practices
#. Reduce the effort of sales staff and purchasing staff to perform their functions
#. Define metrics to evaluate performance and progress
#. Have an alert system of reports of issues that need to be addressed.
#. Require no hardware or other infrastructure or the installation of any software on any Align computer.

# Questions

What is the current cost of 

#. Not buying the correct quantities
#. Not taking into consideration multiple certifications
#. Buying inventory in one operation that is excess inventory in the other operation
#. Time wasted gathering information to create a purchase order

# Conclusion

Align has the expertise in house to participate in the design of a business process and software
to optimize the purchasing and sales operations, there is no need to wait for an IT person who
has much less experience than your director of purchasing and other operations personnel.

A one hour phone call every two weeks is not going to ever get you a design.

I have no doubt that several times every day a sub-optimal purchase results in a expense greater than
the cost of developing a design.

A design is best done by whiteboard meetings, starting with a blank whiteboard.  Even with 25 years 
experience in Aerospace, it would be presumptious, and flat out wrong for me to give a powerpoint 
presentation and say "This is the universal answer to all problems, it will fit your situation"; this
is the approach of someone hawking software.



# Planning Requirements

## Objectives

#. Maximum buyer productivity
#. Eliminate unnecessary purchases
#. Develop a standardized methodology for buyers that is deterministic, with the same input two buyers should come to the same conclusions.
#. Buyers should be able to test the results of a simulation by, for example adding a secondary manufacturer CofC to a lot and replan the part and get the answer back within a second with a new single screen.
#. All scenarios should be stored with the results.
#. Upon acceptance of a scenario the simulation changes should be reported so 
that the source systems can be update.
#. Status of simulation changes
   # Requested
   # Not possible - stops further recommendations to take this action
   # Active - Once a download from the source system reflects this change
#. A report of requested modification not yet completed on source systems
   


## Purchasing Operational Efficiency

### Purchasing Review Board
   Requisitions may be reviewd by the purchasing review board
   * Approval
   * Disapproval  
     * Record disapproval reason for requisitions
       Purchasing review board can select (or create and select) a reason such
as   
       * Review equivalent parts
       * Is onhand under another part
       * insufficient quotations (other vendors may have lower costs)

### Speed up quotations
   * Automatically email vendors request for quotations 
   * Automated quote response
     have the vendors provide a CSV, JSON or XML file with the quote to be
   * automatically uploaded to the system.
   
   For example a vendor could create a spreadsheet with the following columns

   * item_cd
   * quantity
   * manufacturer
   * price 
   * available date
  
   by emailing to quotes@yourco.com these quotes can be automatically loaded
into the system without changes to the legacy system,     

### Buyer information

The buyer should have single screen that shows:

#. Supplies

  #. On hand

  #. Open Purchase Orders

  #. Open Work Orders

#. Demand 

  #. Forecasted
    
    #. Raw

    #. Consumed

    #. Unconsumed

  #. Safety Stock

  #. Work Orders

#. Reserved Inventory

  #. Quarantined

  #. Restricted access (JIT programs, Committed Service Level Agreement Plans)

#. All part numbers in the planning group  

  #. Every part and all equivalents, transitively, that is the equivalents to those equivalents until exhausted.

  #. Customer specific substitutions

#. Approved manufacturer matrix
  Customers down the left, manufacturers across the top 

#. Requisitions

#. Supplier on-time historical metrics

#. Supply ineligibility drill-down

#. Vendor Quotes

#. Time phased inventory position, Pipeline (Global, by Facility, by planner)

#. On hand inventory in aggregate with the ability to open details with a single click

#. Sales history for the last three years in multiple dimensions

  #. Time Dimensions include annual, quarterly and monthly

  #. Ability to see by customer 

#. Existing purchase orders

#. Existing facility transfers in process

#. Detailed reason why supplies are not eligible for a demand that is allocated late or short 

#. A matrix of approved manufactures and customers

#. See the part and all transitive equivalent parts

#. Late or short demands


In Diamond this is all done locally in the web browser with no network requests so it is virtually instaneous.

### Recommendations
#. Purchase orders that can be cancelled
#. Get a manufacturer Certificate of Compliance for existing inventory to satisfy a 
requisition with existing inventory
#. Supply prioritization
   Use buyback inventory before using our inventory for appropriate customers
#. Allocation based pricing
#. Items with a shelf life have oldest allocated first
#. Less valuable items are allocated first
   #. Based on Certifications (dual certed parts have more value)
#. Facility Transfer
#. Supply Pool Transfer
#. Expedite or de-expedite a purchase order

### Alerts
  * Obsolete Inventory
  * Expiring Inventory
  * Purchase Exceeding x% of previous maximum unit price
  * Purchase Exceeding x% of previous minimum unit price
  * Purchase of specified dollars not yet approved
   
## Extensibility

   Any component must be easily plugged in with an alternative implementation
   that is compliant with the corresponding interface,

   * Demand Priority
   * Eligibility Requirements
   * Supply Prioritization
     * Lot value determination 
   * Recommendation Handlers for propagating accepted recommendations to source system
   

# Implementation
  
  * Extract necessary data from legacy systems
  * Load into Advanced Planning
  * Augment with necessary but unavailable information 
  * Run a full plan
  * Review recommendations
    * Accept recommendation (must define Action Handlers)
    Reject recommendation (select reason to be persisted across full reloads)

# Modifications
  * All code is in Java supported by Spring with Hibernate for Object Relation Management, these are widely adoped open source solutions
  * Presentation uses the Model, View Controller approach and the model may be exposed as a JavaBean or XML if one prefers to use XSL.
  * Diamond dependencies are all vastly popular open source but it extremely unlikely anyone will have any need to modify anyy of the open source code
  * I have trained non-programmers to modify Diamond in less than a month.
     
## Questions

#. Inventory Restriction 
   #. How do you restrict availability of inventory for special purposes such as
      * JIT contracts
      * Committed Service Level Agreements
      * Kitting and Assembly
   #. Do you have automated approved manufacturer eligibility?
   #. Do you have prioritization for lots with expiry dates?
   #. How do you calculate the residual cost of goods for broker buys for the
   #. Are you exclusively FIFO or do you consider lots that have lower cost that
satisfies the demand (taking into consideration multiple
certifications, incremental cost of Quality Assurance testing and destructive
tests?), etc.?
quantity that exceeds the customer demand?
#.  Quality Assurance
   Do you have a quality assurance program that supports skip lot testing and 
pre-approved lots ( lots that have already passed the QA requirements for a customer should 
receive higher priority for that customer and lower priority 
for others)    
#. What supply eligibility rules do you have?
#. How do you pin an allocation to a demand ?
#. Does your system recommend when alternate availability is preferable to a
pinned allocation?

# Difference from SAP
  #. Diamond client interface is natively HTML5 and all functionality is available in a simple browser, 
whereas SAP has a cludgy interface, the native interface is client-server, a technology that is embarassingly 
old fashioned and the implementation is poor, at best https://help.sap.com/doc/saphelp_nw70ehp1/7.01.16/en-US/4d/aeae42cd7fb611e10000000a155106/content.htm?no_cache=true
 #. There are no exposed transaction codes in Diamond, everything is menu driven, reducing training time and simplifying operation efficiency.
 #. SAP has a proprietary database, HANA
 #. Diamond is not complicated it uses International Standards for everything, and the most widely adopted technologies, there is no dependency on anything proprietary unless the
oracle database is used.



# SAP on the web

* https://www.brightworkresearch.com/sap/2017/11/best-understand-saps-negative-innovation/

* https://boards.straightdope.com/sdmb/archive/index.php/t-509111.html

* https://www.linkedin.com/pulse/who-knew-sap-could-so-complicated-heather-peyton/
* "We are nowhere near best-in-class, but we are making progress," says Steve Rogers, UK managing director of SAP to an audience of his customers at the German applications firm's annual user gathering at the end of last year. It's not the kind of comment that you expect from a senior executive at a leading software firm. 

* https://www.thirdstage-consulting.com/lessons-from-an-sap-failure-at-lidl/

* https://www.360cloudsolutions.com/top-six-erp-implementation-failures/

* https://www.brightworkresearch.com/saphana/2017/06/22/hana-big-data-equals-big-failure/

