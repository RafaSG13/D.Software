/*
 * Your application specific code will go here
 */
define(['knockout', 'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter', 'ojs/ojurlparamadapter', 'ojs/ojthemeutils', 'ojs/ojmodule-element-utils', 'ojs/ojmoduleanimations', 'ojs/ojarraydataprovider', 'ojs/ojknockouttemplateutils', 'ojs/ojknockout', 'ojs/ojmodule-element'],
  function(ko, CoreRouter, ModuleRouterAdapter, KnockoutRouterAdapter, UrlParamAdapter, ThemeUtils, moduleUtils, ModuleAnimations, ArrayDataProvider, KnockoutTemplateUtils) {
     function ControllerViewModel() {
      var self = this;

      self.KnockoutTemplateUtils = KnockoutTemplateUtils;

      // Handle announcements sent when pages change, for Accessibility.
      self.manner = ko.observable('polite');
      self.message = ko.observable();

      document.getElementById('globalBody').addEventListener('announce', announcementHandler, false);

      function announcementHandler(event) {
        setTimeout(function() {
          self.message(event.detail.message);
          self.manner(event.detail.manner);
        }, 200);
      };

      // Save the theme so we can perform platform specific navigational animations
      var platform = ThemeUtils.getThemeTargetPlatform();

      var navData = [
        { path: '', redirect: 'login' },
        { path: 'login', detail: { label: 'Login', iconClass: 'oj-ux-ico-bar-chart' } },
        { path: 'register', detail: { label: 'Crear cuenta', iconClass: 'oj-ux-ico-fire' } },
        { path: 'setNewPassword', detail: { label: 'Cambiar contraseña', iconClass: 'oj-ux-ico-fire' } },
        { path: 'product', detail : { label : 'Producto'} },     
        { path: 'payment', detail : { label : 'Pago'} },
        { path: 'corder', detail : { label : 'Pedido'} },
        { path: 'about', detail: { label: 'About', iconClass: 'oj-ux-ico-information-s' } }
      ];
      // Router setup
      var router = new CoreRouter(navData, {
        urlAdapter: new UrlParamAdapter()
      });
      router.sync();
      
      this.router = router;

      this.moduleAdapter = new ModuleRouterAdapter(router, {
        animationCallback: platform === 'android' ?
          function(animationContext) { return 'fade' }
          : undefined
      });

      this.selection = new KnockoutRouterAdapter(router);

      // Setup the navDataProvider with the routes, excluding the first redirected
      // route.
      this.navDataProvider = new ArrayDataProvider(navData.slice(1), {keyAttributes: "path"});

      // Used by modules to get the current page title and adjust padding
      self.getHeaderModel = function() {
        // Return an object containing the current page title
        // and callback handler
        return {
          pageTitle: self.selection.state().detail.label,
          transitionCompleted: self.adjustContentPadding
        };
      };

      // Method for adjusting the content area top/bottom paddings to avoid overlap with any fixed regions.
      // This method should be called whenever your fixed region height may change.  The application
      // can also adjust content paddings with css classes if the fixed region height is not changing between
      // views.
      self.adjustContentPadding = function () {
        var topElem = document.getElementsByClassName('oj-applayout-fixed-top')[0];
        var contentElem = document.getElementsByClassName('oj-applayout-content')[0];
        var bottomElem = document.getElementsByClassName('oj-applayout-fixed-bottom')[0];

        if (topElem) {
          contentElem.style.paddingTop = topElem.offsetHeight+'px';
        }
        if (bottomElem) {
          contentElem.style.paddingBottom = bottomElem.offsetHeight+'px';
        }
        // Add oj-complete marker class to signal that the content area can be unhidden.
        // See the override.css file to see when the content area is hidden.
        contentElem.classList.add('oj-complete');
      }
    }

    return new ControllerViewModel();
  }
);
