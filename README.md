Deployer is a create library for addon-developers to simplify logistics

# Features

## Stock Inventory System
<div style="display: flex; align-items: flex-start; gap: 20px;">
  <img src="image_0.png" alt="A basic fluid packaging system implementation" style="flex-shrink: 0;">

  <div style="flex: 1;">
    Stock Inventory types is a new addition added to follow the 6.0 create update. You can register anything that you want to carry with packages, and deployer will simplify the process. Fluids, Energy, whatever you want!
  </div>
</div>

## Gauge Creation API

<div style="display: flex; align-items: flex-start; gap: 20px;">
  <div style="flex: 1;">
    Deployer provides a comprehensive API for creating custom gauges, evolved from the Extra Gauges addon.
    Deployer handles all the hard part, making it possible for different gauges to stay in the same block!
    <br><br>
    You can handle connections coming in and coming out from the gauge, and handle everything you want!
  </div>
  <img src="image_1.png" alt="Goggle information displayed for an entity" style="flex-shrink: 0;">
</div>

## Extended Goggle Information
<div style="display: flex; align-items: flex-start; gap: 20px;">
  <div style="flex: 1;">
    Create's vanilla goggle overlay system only supports block entities, limiting what information can be displayed when players look at blocks or entities. Deployer extends this functionality through the <code>DeployerGoggleInformation</code> interface, enabling goggle information display for regular blocks without block entities and for entities in the world.
    <br><br>
    It's also possible to register your own goggle information through <code>ClientRegisterHelpers</code>, under your specific conditions
  </div>
  <img src="image_2.png" alt="Goggle information displayed for an entity" style="flex-shrink: 0;">
</div>

## Custom Stock keeper tabs & Redstone requester tabs

<div style="display: flex; align-items: flex-start; gap: 20px;">
  <div style="flex: 1;">
    With deployer you can create custom stock keeper and redstone requester tabs. Creating a tab is not necessarily linked to creating a stock inventory type. You can stor any data inside your tab and use it for whatever you want. We already have some ideas in mind for our mods <strong>Create: Extra Gauges</strong>!
  </div>
  <img src="image_3.png" alt="Two new tabs created with deployer" style="flex-shrink: 0;">
</div>

<div style="display: flex; align-items: flex-start; gap: 20px;">
  <img src="image_4.png" alt="Two new tabs added to the redstone requester" style="flex-shrink: 0;">

  <div style="flex: 1;">
    On the other side, redstone requesters can also have tabs which are specifically intended to order items!
  </div>
</div>

## Optimization and fix
We also include a bunch of fixes and optimizations, and we want to focus the second part of this API on that, improving some useless and expensive algorithms that create for some reason has