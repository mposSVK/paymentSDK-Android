document.addEventListener('DOMContentLoaded', function () {
	var hashcache = {};
    var langcache = {};
    var ariacache = {};
	Array.prototype.forEach.call(document.querySelectorAll('ul.nav.nav-tabs'), function(tablist) {
    Array.prototype.forEach.call(tablist.querySelectorAll('li.nav-item'), function(tabitem) {
      Array.prototype.forEach.call(tabitem.querySelectorAll('a'), function (anchor) {
        var lang = anchor.getAttribute("data-lang");
        var aria = anchor.getAttribute("aria-controls");
        var obj = {
          tabitem: tabitem,
          anchor: anchor,
          hash: anchor.hash,
          lang: lang,
          aria: aria
        };
        hashcache[anchor.hash] = obj;
        ariacache[aria] = obj;
        if (!langcache.hasOwnProperty(lang)) {
          langcache[lang] = [];
        }
        langcache[lang].push(obj);
		});
    });
	});
    Array.prototype.forEach.call(document.querySelectorAll('div.tab-pane'), function(tabpane) {
        ariacache[tabpane.id].pane = tabpane
	});
	window.addEventListener('hashchange', onhashchange);

	onhashchange();

    function onhashchange() {
        var lang = hashcache[location.hash].lang;
        Array.prototype.forEach.call(Object.keys(langcache), function(maybelang) {
        if (lang === maybelang) {
            Array.prototype.forEach.call(langcache[maybelang], function(ooo) {
            Array.prototype.forEach.call([ooo.tabitem, ooo.pane], function(obj) {
                classes = obj.className.split(' ');
                if (classes.indexOf('active') === -1) {
                classes.push('active');
                obj.className = classes.join(' ');
                }
            });
            });
        } else {
            Array.prototype.forEach.call(langcache[maybelang], function(ooo) {
            Array.prototype.forEach.call([ooo.tabitem, ooo.pane], function(obj) {
                classes = obj.className.split(' ');
                var idx = classes.indexOf('active')
                if (idx > -1) {
                delete classes[idx];
                obj.className = classes.join(' ');
                }
            });
            });
        }
        });
    }
});