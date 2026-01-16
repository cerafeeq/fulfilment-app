# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
Cost allocation in a multi-warehouse fulfillment environment presents the challenge of fairly distributing shared costs (labor, inventory, transportation, overhead) across warehouses, products, and stores. The fundamental complexity lies in balancing accuracy versus practicality: while detailed activity-based costing provides precise attribution, it requires significant investment in systems and overhead. Most warehouse costs (60-80%) are indirect/shared costs like rent, management salaries, and utilities, making the choice of allocation methodology critical—different approaches (volume-based, space-based, activity-based, revenue-based) yield vastly different results and profitability pictures. Success depends on aligning the granularity of cost tracking with the business decisions it needs to support, while ensuring stakeholders understand and trust the chosen methodology.
The key consideration is determining what business decisions this data will drive and what level of precision is truly needed. A transparent, simple model that stakeholders understand and accept is often more valuable than a complex, highly accurate system that nobody trusts or uses. Additionally, given the existing constraints (max 2 warehouses per product-store combination, max 3 warehouses per store, max 5 products per warehouse), cost data could become strategic in optimizing these associations and informing expansion decisions.

**Key Questions to Explore**
- What is the primary purpose of cost tracking—profitability analysis, pricing decisions, operational optimization, or compliance reporting?
- What specific business decisions will this data enable (e.g., discontinuing unprofitable product-store combinations, warehouse expansion planning, pricing negotiations)?
- What is the acceptable trade-off between accuracy and complexity?
- What level of cost transparency is needed for different stakeholders (finance team, operations, store managers, executives)?

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
Cost optimization in fulfillment operations requires a systematic approach to identifying inefficiencies and waste while maintaining or improving service quality to stores. The challenge lies in understanding that not all cost reductions are created equal—some optimizations (like better warehouse space utilization) deliver sustainable savings with minimal risk, while others (like reducing labor during peak periods) may compromise service levels and damage business relationships.

**Key Questions to Explore**
- What is the current cost structure breakdown across our warehouses?
- Do we have baseline metrics for key performance indicators: cost per unit shipped, cost per picking hour, transportation cost per mile, warehouse utilization rates, order accuracy, delivery time?
- What are the current service level agreements (SLAs) with stores, and what is our actual performance against these commitments?
- Where are the biggest cost buckets, and which areas show the highest variability or inefficiency signals?

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
Integrating a Cost Control Tool with existing financial systems (ERP, accounting software, payroll systems, procurement platforms) is critical for ensuring data accuracy, eliminating manual data entry, and providing real-time visibility into fulfillment costs. The fundamental challenge lies in bridging operational systems (warehouse management, transportation) with financial systems (general ledger, accounts payable, budgeting) which often have different data models, update frequencies, and business logic. Successful integration requires not just technical connectivity but also alignment on data definitions, timing of updates, reconciliation processes, and organizational governance around master data.The key consideration is determining the integration architecture: should it be real-time event-driven synchronization, scheduled batch processing, or a hybrid approach? Real-time integration provides immediate cost visibility but increases system complexity and coupling, while batch processing is simpler but creates temporal data inconsistencies. Additionally, the integration must handle bidirectional data flows—operational data (transactions, activities) flowing into finance, and financial data (budgets, cost allocations, approved rates) flowing back to operations. Without proper integration, companies face manual reconciliation efforts, delayed cost recognition, duplicated data entry errors, and inability to make timely operational decisions based on current financial reality.

**Key Questions to Explore**

- What is the primary business driver for this integration—is it financial reporting accuracy, operational cost visibility, audit compliance, or decision-making speed?
- What decisions need to be made with cost data, and what is the required latency (real-time vs. daily vs. monthly)?
- Who are the stakeholders consuming this integrated data (CFO/finance team, operations managers, warehouse managers, executives, auditors)?
- Are there regulatory or compliance requirements that dictate integration requirements, audit trails, or data retention?

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
Budgeting and forecasting for fulfillment operations involves predicting future costs across warehouses, products, and stores while accounting for seasonality, volume fluctuations, network changes, and cost driver variability. The fundamental challenge is that fulfillment costs are highly variable—driven by order volumes that fluctuate daily, seasonal demand patterns, labor market conditions, fuel prices, and strategic decisions about warehouse expansion or automation. Unlike manufacturing with stable production schedules, fulfillment operations must budget for uncertainty while maintaining service commitments to stores. This requires sophisticated models that separate fixed costs (warehouse rent, permanent staff, equipment depreciation) from variable costs (temporary labor, packaging materials, transportation fuel) and incorporate step-function costs (adding a warehouse shift, leasing additional trucks) that don't scale linearly. 

The key consideration is balancing forecast accuracy with operational flexibility. Overly precise forecasts create false confidence and rigid budgets that can't adapt to reality, while overly conservative forecasts lead to resource inefficiency. The system must support multiple forecast horizons: long-range strategic planning (3-5 years for warehouse network decisions), annual budgeting (aligned with financial planning cycles), quarterly re-forecasting (adjusting for actual performance), and short-term operational forecasts (next 4-13 weeks for staffing and inventory). Additionally, forecasts must be multi-dimensional—not just total costs but costs by warehouse, product line, store, and cost category—to enable actionable resource allocation decisions. Without effective budgeting and forecasting, companies face either cost overruns from insufficient planning or service failures from under-resourced operations.

**Key Questions to Explore**

- What are the primary business decisions that budgets and forecasts will inform (warehouse expansion/closure, hiring plans, pricing to stores, capital investment in automation)?
- What is the planning horizon for different stakeholder needs (CFO needs annual budget, Operations needs quarterly staffing plans, Warehouse managers need weekly schedules)?
- What is the current budgeting process and pain points (takes too long, too inaccurate, too aggregated, unable to adapt to changes)?
- What level of forecast accuracy is required vs. achievable (within 10%? 20%?) and how does this vary by time horizon and cost category?

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
Warehouse replacement presents a unique cost control challenge where a company must manage parallel operations during transition (running costs for both old and new warehouses), preserve historical cost baselines for performance comparison, maintain business continuity without service disruptions, and ensure the new warehouse achieves its targeted cost improvements while avoiding the common pitfall of cost overruns during ramp-up. The fundamental complexity is that the new warehouse will inherit the Business Unit Code of the old one, creating a discontinuity in the cost data—the same identifier will represent two physically different facilities with potentially different cost structures, capacities, and operational characteristics. This requires careful data management to separate "legacy costs" (old warehouse) from "new costs" while maintaining a continuous view for trend analysis and budget comparisons. 

The key consideration is understanding why the warehouse is being replaced and what cost implications flow from that reason. Is it capacity expansion (new warehouse is larger, will have higher absolute costs but better unit costs at scale)? Is it cost reduction (moving to lower-rent area, automating to reduce labor)? Is it operational efficiency (better layout, modern equipment reducing handling costs)? Or is it forced replacement (lease expiring, building obsolete)? Each scenario has different cost trajectories and success metrics. Without preserving historical cost data, the company cannot validate whether the replacement achieved its financial objectives, cannot identify if new problems emerge, and cannot learn from the transition for future warehouse projects. The archived warehouse's cost history becomes the baseline against which the new warehouse's performance is measured, making its preservation essential for accountability and continuous improvement.

**Key Questions to Explore**

- What is the primary driver for warehouse replacement—capacity constraints, cost reduction, lease expiration, operational efficiency, geographic repositioning, or consolidation?
- What are the specific cost-related objectives for the new warehouse?
- What is the business case ROI for the replacement, and how will we measure whether it was achieved?
- What service level commitments exist during the transition (can we tolerate any disruption to stores?), and what's the cost of service failures?
- How does this replacement fit into broader warehouse network strategy (is this one of many planned replacements, a pilot for future modernization)?


## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
