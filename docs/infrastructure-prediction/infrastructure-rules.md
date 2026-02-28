# Rules for Infastructure Prediction

We should develop a list of requirements and rules for the prediction algorithm. Information
gathered on this can be found at the following sources:

[Think Broadband](https://www.thinkbroadband.com/news/4440-openreach-gearing-up-for-fttp) provides
- "Usually five or six properties (maximum 12) connected to one Manifold, which will be underground 
  and no further than 60m from the properties."
- "Each manifold links back to a larger splitter node via fibre. Each splitter note supporting 32 
  manifolds."
- "Each splitter links back to an aggregation point, where multiple splitter links arrive."
- "The aggregation point uses another larger fibre to link back to the Next Generation Access 
  Handover node."
- **Conclusion**
  - The point of connection from building to the manifold or CBT should be no further than 60m
  - Splitters support up to 32 manifolds which each support up to 12 buildings. 
  - This network should branch back to a "access handover point".

[Think Broadband 2](https://www.thinkbroadband.com/guides/fibre-fttc-ftth-broadband-guide) shows the
difference between Fibre to the Cabinet (FTTC) and Fibre to the Home/Premises (FTTH/FTTP).
- FTTC runs fibre from the telephone exchange to street cabinets where it is then connected to
  copper phone lines to distribute broadband.
- FTTH/FTTP provides an end to end fibre optic connection this invovles manhole aggregation and
  splitter nodes.
- **Conclusion**
  - This could be an overcomplication for our project but outlines the different distribution
    methods of fibre. 

[Prysmian](https://uk.prysmian.com/media/news/what-is-a-connectorised-block-terminal) shows the use
of Connectorised Block Terminals (CBT). 
- "The CBT is connected back to the exchange via fibre-optic cable and must be installed in an 
  underground chamber or attached to a telegraph pole close to the premises where FTTP is being 
  installed."
- "CBT also comes in three sizes with 4,8 and 12 ports".
- **Conclusion**
  - From telegraph poles with CBT's a maximum of 12 buildings can be connected.

[BT Community](https://community.bt.com/t5/BT-Fibre-broadband/FTTP-What-the-maximum-cable-run-from-the-pole-to-house-and/td-p/2204005)
An expert posted on the forums that "The maximum span allowed between the pole and your house is 
68m"
- **Conclusion**
  - The length of cable from pole to house should not exceed 68m.

[Openreach](https://www.openreach.com/content/dam/openreach/openreach-dam-files/images/fibre-broadband/fibre-for-developers/guides-and-handbooks/oct-2019-update/Quick%20guide%20Joint%20boxes,%20footways%20and%20frames%20&%20covers%20V2%20web.pdf)
fibre cannot be bent but with the use of joint boxes it can.
- **Conclusion**
  - If the fibre has to turn more than 45 degrees then a joint box should be placed.

[UnionFiber](https://www.weunionfiber.com/optical-splitters-a-deep-dive-into-split-ratios-and-splitting-architectures-for-ftth-pon-network/)
shows two splitting architectures:
- Centralized Splitting Architecture: works as a point to multipoint star topology. Ideas for urban
  and suburban areas.
- Cascading Splitting Architecture: Smaller splitters (1:4, 1:8) split to eachother to get a total
  split ratio (1:4 to 1:8 creates 1:32). Ideal for rural areas.

At the present moment it is also under assumption that:
- Fibre should not be present on any motorways at all.
- Fibre can use A roads and B roads as fibre is usually present on footpaths or the road verge.
- Research needs to be done on intersection behaviour e.g. Fibre should never cross a multi lane 
  road at an angle.

## Requirements
- The network should avoid motorways.
- Joint Boxes should be placed where fibre has to turn more than 45 degrees.
- Telegraph poles can be connected to 12 buildings max.
- The cable from telegraph poles to buildings can be no longer than 68m.
- Splitters support maximum 32 buildings with recommended at 30.
- Splitters can be linked to aggregation points.
- The network should begin at a telecomunications distribution point (Exchange).
- Use Centralized Splitting Architecture (Maybe Cascading for rural areas).