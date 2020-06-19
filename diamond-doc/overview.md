Introduction
============

The Diamond Advanced Planning System (APS) is the most feature-rich
inventory planning system in the market today. There is no other
planning engine out there that can match the Diamond APS in terms of
speed and versatility. Diamond distribution was designed to address the
supply chain planning and execution requirements of distributors.
Diamond was custom designed to address the rigorous specifications of
the Aerospace industry, which imposes significantly tighter controls and
tracking functionality than a normal distribution environment.




Features Supports Synchronous and Asynchronous planning queues. Parts in
the synchronous planning queue are planned the instant they are put into
the queue. The planning engine plans parts in the asynchronous planning
queue when it reaches the top of the queue. The asynchronous planning
queue is activated every time a part is inserted into the queue.

Demand Types
------------

Supports multiple types of Demand. Demand types supported are

-   Firm Customer Order

-   Work Orders (Components of Work Orders)

-   Safety Stock

-   Forecast

Supply Types
------------

Supports multiple types of Supply. Supply Types supported are

-   On-Hand at Location

-   In-Transit from another Location

-   Purchase Orders

-   Work Orders

Supply Partitioning
-------------------

Physical Segregation of Inventory is done using Facilities

Logical Segregation of Inventory within a facility is done using Supply
Pools

Sourcing rules attached to every demand specify which supply to consume
and the order in which to consume.

The most commonly used database entities are cached in memory. Cached
entities include Facilities, Supply Pools, Sourcing Rules,
Certifications, Business Calendar, Equivalent Part Numbers, Customer
specific Substitutes, Global Substitutes, Organizations, Forecast
Groups, Application Control variables and Purchase Order Equivalents.
Caching saves on the expensive operations of database access for the
most commonly used entities.

Triggers on cached tables signal the Planning engine to refresh those
values

Part Number, substitutes and equivalents are all planned together

Qualifying Inventory
--------------------

-   Ability to specify the revision level required for Sales Order and
    Work Order, Safety Stock and Forecast. The revision level hierarchy
    setup in the Parts Master will automatically allocate a higher
    revision level if the requested revision level is not available.

-   Ability to specify certification requirements for Sales Order and
    Work Order and associate it with the demand.

-   Certification requirements for Safety Stock and Forecasts are picked
    up from the Customer Master

-   Each certification has a weight associated with it. APS uses
    qualifying lots with the lowest weight to satisfy any demand.

-   Ability to specify the manufacturer required at the Demand level or
    can specify a list of approved manufacturers at the Customer Level.
    Customer List can be set to Include or Exclude approved
    manufacturers

-   Ability to specify Country of Origin on the Sales Orders

-   Ability to specify “manufactured after” or “must not expire before”
    date on Sales Orders for parts with a shelf life.

-   Automatically excludes expired part numbers

-   Kits can be setup in the system to not have mixed manufacturer lots
    allocated to them for any single kit. APS automatically determines
    the lot quantity and how many full kits it can satisfy and then
    switches to the next available lot to satisfy any remaining kits.

Demand Prioritization
---------------------

Demand Prioritization module compares every demand for a given set of
part numbers and sorts them in the following order

-   Sales Orders

-   Work Orders

-   Safety Stock

-   Forecasts

Preserve Allocations
--------------------

Once sorted, Demand Prioritization also preserves On-hand allocations to
Customer Orders and Work Orders within lead-time. All allocations to
Safety Stock are preserved. Preserving existing allocations prevents
demands that have been put in later from taking stock away from already
existing orders. Safety Stock demand is allocated to Customer Orders and
Work Orders within the same forecast groups if there is a shortage.
Forecast Demands are reduced by the quantity of open Customer Order
demands for the same month for the same forecast group, which stops over
stating of demand for a given month.

Supply Prioritization
---------------------

Supply prioritization uses the sourcing rules to determine which
supplies to use to satisfy a given demand. Once the qualifying supplies
are identified, they are then sorted based on the type of the demand and
type of the supply. An example for this would be to use the oldest lots
to satisfy open sales orders while using the newest possible lots to
satisfy safety stock demand. Since safety stock demand is never shipped,
it blocks the newer inventory allowing the older lots to ship out before
the newer ones. Supply prioritization changes the FIFO order for parts
based on the settings in the Parts Master. Parts with a shelf life can
be consumed based on the Manufacture Date or on the Expiration Date of
the lots. Supply Prioritization also automatically relaxes all the
constraints on the demand when allocating consignment or buyback supply
to a demand. Buyback and consignment supply is stock received from the
customer that is shipped back to them when they need it. This stock is
always deemed to meet customer requirements.

-   Allocations against on-hand supply are classified as Firm or Planned
    depending on if the on-hand supply is readily available in the
    primary facility or if the on-hand supply is a planned facility
    transfer or a processed facility transfer in transit to the primary
    facility. The primary facility for a demand is identified based on
    the sourcing rule used to determine the eligible supply for the
    demand.

-   APS automatically creates work orders for kits. Since APS supports
    multi-level Bills of Material, it creates work orders for sub-kits
    and re-plans all the items recursively till all the demands for kits
    have been allocated either to on-hand inventory or to a work order.

-   Purchase Orders schedules that are late are automatically padded by
    a user-defined factor and pushed forward. This enables the system to
    provide realistic availability dates for the demands which are
    allocated to those PO schedules.

-   Automatically allocates demands to a Purchase Order if the demand is
    “X” days out in the future and there is a PO Schedule coming
    available “X” days before the demand is due. The value of “X” is
    read in from a Control table.

-   APS will suggest a optimum reschedule date for the PO Schedules that
    have allocations against demands that need to be expedited or
    rescheduled to come in at a date later than the current promise date
    provided by the vendor

-   APS will also suggest cancellation of PO schedules that are not
    needed to meet any demand that is present in the system.

Allocation logic fully traceable. An XML or JSO log file may be created created
for each item group planned detailing each demand and all supplies,
which ones were allocated and which ones were rejected and the reason
for rejection.

Ability to bind a given supply to a given demand as long as the supply
is qualified for the demand. Allocations once bound are held bound
unless unbound by the user.

APS Output
----------

The Inventory planning process is the most impacted by running Diamond
APS. The APS output is fully web-based and provides the Inventory
planners with all the information required to make sound buying
decisions. Inventory planners have the ability to lookup shortfalls by
specifying a whole range of filter conditions. Listed below are the
details of the outputs provided by Diamond APS.

Shortages
---------

Diamond APS classifies shortages into the following categories

-   Unallocated Customer Orders

-   Unallocated Work Orders

-   Unallocated Safety Stock

-   Unallocated Forecasts

-   Customers Orders allocated beyond the requested date

-   Work Orders allocated beyond the requested date

-   Safety Stock allocated beyond the requested date

-   Forecasts allocated beyond the requested date

Users can choose a combination of any of these shortage conditions and
then apply the following filters to narrow their search

Part Number Mask (A Wildcard search for a range of Part Numbers)

The Part Category. Normally buyers are responsible for purchasing a
certain category or categories of parts. This help narrow the results to
only the parts they are responsible for purchasing. Within Lead Time.
This restricts the output only to shortages that occur within the lead
time for a given Part Outstanding Vendor Quotes less than “X” days. This
further narrows the search and ignores the parts that have outstanding
vendor quotes that are less than “X” days old. Vendors normally take
some time to respond to quotes and this help buyers from seeing the same
parts on the list even after they have worked on it. Planning Horizon
End Date. This restricts the list of parts being shown to have shortages
only within the Date specified here Buyer. This only shows the parts the
specified buyer is responsible for buying. Customer Code. This restricts
the list of parts only to the shortages for the customer specified.
Maximum Part to display. The default is set to 100. The users can
specify any number greater than 0.

Once the search criteria is specified, APS will go through its planning
results and find all the part numbers that match the specified search
criteria. It will then sort them into 4 groups.

-   Unallocated or Late Customer Orders

-   Unallocated or Late Work Orders

-   Unallocated or Late Safety Stock

-   Unallocated or Late Forecasts

A Part will only appear in one of these groups, the group in which the
part has the earliest shortage. Each part then links off into a 12-month
time-phased view of the Demand and Supply outlook. The time-phased
output has columns for past due, current, 12 months starting with the
current month and a column for demands and supplies coming in beyond 12
months. This page also provides links to see the following information
The Allocation Trace Log. This file contains a complete log of the
allocation process for the part and all its substitutes and equivalents.
Provides a listing of all the supplies available to allocation and also
lists each demand followed by which supplies were allocated to it and
which supplies were not and also provides a reason for ineligible
supplies for the Demand.

Work Book. The Work Book create an excel spread sheet with the following
information about the Part. The On-Hand inventory summary by Lot,
Facility and Supply Pool, Customer and Vendor Quotes for the Part, Open
Purchase Orders, Open Sales Orders, Shipments and a 12 month forecast by
forecast group. The work book can then be saved of and helps buyers
maintain a log of the demand/supply scenario at the time they made any
purchase. A listing of all available supply and which demands it is
allocated to A listing of all demands and what supplies are allocated to
it Approved Manufacturers. Provides a cross-tab view of the customers
and their approved manufacturers. Helps buyers make a choice of buying
from the manufacturer that will satisfy the most number of customers.

Customer Quotes Listing

Vendor Quotes Listing

Rescheduling information for any PO Schedules in the system Shipment
Details. Provides a listing of all the shipment of this part to any
customer. Forecast History. Provides a time-phased display of the
forecast history by forecast group for the given part. Also lists the
forecasts by forecast group. Shipment Summary. Provides a year-month
cross-tab of shipment of this part. Shipment Summary by Customer.
Provides a year-month cross-tab of shipment by customer of this part.
Ability to lookup shortfalls by the following

-   Item Certification

-   Manufacturer and vendor certification

-   Lists shortfalls based on explicit certifications requested on the
    Demand. A review of these might help the user offer

-   Country of Origin

-   Approved Manufacturer

-   Explicit Manufacturer Requested on the Demand

-   Revision Level

-   Re-certification opportunities

Ability to lookup Rescheduling Requirements in the following groups

#### Purchase Orders to be Expedited

Provides a summary by Vendor of the Purchase Orders that need to be
expedited to meet current and forecasted demand. Users can then drill
down into each Vendor and look at each individual PO Schedules need to
be expedited and the system also suggested expedite date taking into
account the time required to process the receipt after it arrives.

#### Purchase Orders to be Rescheduled

Provides a summary by Vendor of the Carrying Cost and the Cancelable
Cost for PO Schedules that can either be pushed out for cancelled. Users
can then drill down into each Vendor and look at each individual PO
Schedule that needs to be rescheduled. For PO schedules that need to be
pushed out, the system suggests the new date by which they are required.

#### Purchase Orders to be Cancelled

Provides a summary by Vendor of the cancelable cost of outstanding PO
Schedules. Users can then drill down into each Vendor and see the
individual PO Schedules that need to be cancelled.
