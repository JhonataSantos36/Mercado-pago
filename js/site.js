
var site = (function(){
  var smoothScrollingTo,
      fixedMenu,
      winHeight = $(window).height(),
      docContent = $('.main-content'),
      devicePreview,
      defaultScreen;

  window.rAF = (function(){
    return  window.requestAnimationFrame       ||
            window.webkitRequestAnimationFrame ||
            window.mozRequestAnimationFrame    ||
            function( callback ){
              window.setTimeout(callback, 16);
            };
  })();

  /* Header menu toggle for mobile */
  $("#menu-toggle").click(function(e) {
      e.preventDefault();
      $(this).toggleClass("active");
  });

  // smooth scroll
  $('a[href*=#]:not([href=#])').click(function() {
    if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
      if (target.length) {
        smoothScrollingTo = '#' + target.attr('id');
        $('html,body').animate({ scrollTop: target.offset().top }, 100, 'swing',
          function() {
            if(docContent) {
              previewSection(smoothScrollingTo);
            }
            smoothScrollingTo = undefined;
          });
        return false;
      }
    }
  });

  // left menu link highlight
  var leftMenu = $('.left-menu');
  var activeLink = leftMenu.find('[href="' + window.location.pathname + '"]');
  activeLink.parents('li').addClass("active");

  leftMenu.find('.api-section').click(function(){
    if( $(this).attr('href') == '#' ) {
      $(this).closest('.left-menu').find("li").removeClass('active');
      $(this).closest('li').toggleClass('active');
      return false;
    }
  });


  /* Fixed left menu */
  (function() {
    var activeId;
    fixedMenu = $('.docked-menu');
    if(fixedMenu.length) {

      var locationhref = document.location.href;
      var lastPathSegment = locationhref.substr(locationhref.lastIndexOf('/') + 1);
      lastPathSegment = lastPathSegment.split('#');
      lastPathSegment = lastPathSegment[0];  

      var targets = fixedMenu.find('.active-menu').find('a');
      targets.each(function() {
        var href = $(this).attr('href');
        if(href && href.indexOf('#') > -1) {
          href = href.split('#');
          if(href[0] == lastPathSegment) {
            href = "#" + href[ href.length - 1 ];  
            $(this).attr('href', href);
          }
        }
      });

      var scrollSpyOffset = 40;
      if( $(document.body).hasClass("device-preview-page") ) {
        scrollSpyOffset = 300;
      }

      $(document.body).scrollspy({ target: '.docked-menu', offset: scrollSpyOffset });

      var fixedMenuTop = fixedMenu.offset().top;
      var menuTopPadding = 20;
      fixedMenu.css({
        top: menuTopPadding + 'px'
      });

      function docScroll() {
        var win = $(window);
        var scrollTop = win.scrollTop();
        var winWidth = win.width();
        if(scrollTop + menuTopPadding > fixedMenuTop && winWidth >= 768) {
          // middle of the page
          if(!fixedMenu.hasClass("fixed-menu")) {
            fixedMenu
              .css({
                width: fixedMenu.width() + 'px',
                top: '20px'
              })
              .addClass("fixed-menu");
          }
        } else {
          // top of page
          if(fixedMenu.hasClass("fixed-menu")) {
            fixedMenu
              .removeClass("fixed-menu")
              .css({
                width: 'auto',
                top: '20px'
              });
          }
          if(scrollTop < 200) {
            $('.active').removeClass(".active");
          }
        }
      }
      $(window).resize(function() {
        //preFooterTop = $('.pre-footer').offset().top;
        winHeight = $(window).height();
        fixedMenu
            .removeClass("fixed-menu")
            .css({
              width: 'auto'
            });
        docScroll();
      });
      var docScrollGovernor;
      function governDocScroll(){
        clearTimeout(docScrollGovernor);
        docScrollGovernor = setTimeout(docScroll, 15);
      }
      $(window).scroll(governDocScroll);

      function scrollSpyChange(e) {
        if(smoothScrollingTo || !docContent) {
          window.history.replaceState && window.history.replaceState({}, smoothScrollingTo, smoothScrollingTo);
          return;
        }

        var id;
        if(e.target.children.length > 1) {
          // this is a top level nav link
          var activeSublinks = $(e.target).find('.active');
          if(!activeSublinks.length) {
            // no children are active for this top level link
            id = e.target.children[0].hash;
          }
        } else if(e.target.children.length === 1) {
          // this is a sub nav link
          id = e.target.children[0].hash;
        }

        if(id) {
          if(devicePreview) {
            window.rAF(function(){
              previewSection(id);
            });
          } else {
            var activeSection = $(id);
            if(activeSection.length) {
              window.rAF(function(){
                docContent.find('.active').removeClass('active');
                activeSection.addClass("active");
              });
            }
          }
          window.history.replaceState && window.history.replaceState({}, id, id);
        }
      }
      fixedMenu.on('activate.bs.scrollspy', scrollSpyChange);
    }
  })();

  // initDevicePreview
  (function() {
    /* Fixed device preview on the docs page */
    devicePreview = $('.device-preview');
    if(devicePreview.length) {
      var orgDeviceTop = devicePreview.offset().top;

      function onScroll() {
        if($(window).scrollTop() > orgDeviceTop) {
          if( !devicePreview.hasClass('fixed-preview') ) {
            devicePreview
              .css({
                left: Math.round(devicePreview.offset().left) + 'px'
              })
              .addClass("fixed-preview");
            }
        } else {
          if( devicePreview.hasClass('fixed-preview') ) {
            devicePreview
              .removeClass("fixed-preview")
              .css({
                left: 'auto'
              });
          }
        }

      }

      var scrollGovernor;
      function governScroll() {
        clearTimeout(scrollGovernor);
        scrollGovernor = setTimeout(onScroll, 15);
      }

      $(window).resize(function(){
        devicePreview
            .removeClass("fixed-preview")
            .css({
              left: 'auto'
            });
        onScroll();
      });
      $(window).scroll(governScroll);

      onScroll();

      var firstSection = docContent.find('.docs-section').first();
      if(firstSection.length) {
        previewSection( '#' + firstSection[0].id, true );
      }

      // manually add the activated CSS like how ionic does it
      devicePreview.on('mousedown', function(e){
        if(e.target.classList && e.target.classList.contains('button')) {
          e.target.classList.add('activated');
        }
      });

      devicePreview.on('mouseup', function(e){
        devicePreview.find('.activated').removeClass('activated');
      });

    }
  })();


  function previewSection(id) {
    var activeSection = $(id);
    if(!activeSection.length || !devicePreview) return;

    var title = activeSection.find('h1,h2,h3').first();
    activeId = activeSection.attr('id');
    docContent.find('.active:not(.tab-item)').removeClass('active');
    activeSection.addClass("active");

    devicePreview.find('.active-preview').removeClass('active-preview');
    var docExample = activeSection.find('.doc-example');
    if( docExample.length ) {
      // this
      var exampleId = 'example-' + activeId;
      var examplePreview = $('#' + exampleId);
      if(examplePreview.length) {
        // preview has already been added
        window.rAF(function(){
          examplePreview.addClass('active-preview');
        });
      } else if(devicePreview) {
        // create a new example preview
        devicePreview.append( '<div id="' + exampleId + '" class="ionic-body">' + docExample.html() + '</div>' );
        window.rAF(function(){
          $('#' + exampleId)
            .addClass('active-preview')
            .find('a').click(function(e){
              // Activates tabs in tab CSS demo.
              if ($(this).hasClass('tab-item')) {
                $(this).siblings('.tab-item').removeClass('active');
                $(this).addClass('active');
              }
              return false;
            });
        });
      }

    } else {
      window.rAF(function(){
        if(!defaultScreen) {
          defaultScreen = devicePreview.find('.default-screen');
        }
        defaultScreen.addClass('active-preview');
      });
    }
  }

})();
