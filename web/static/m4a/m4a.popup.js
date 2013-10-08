(function ($, m4a) {
    m4a.popup = {};

    $.extend(m4a.popup, {
        $popup: $('#popup'),
        $content: $('#popup div.content'),
        $contentData: $('#popup div.content div.data'),
        closedPopup: true,

        init: function() {
            this.bindEvents();
        },


        bindEvents: function() {
            var context = this;

            $(document).keyup(function(e) {
                if (e.keyCode == 27) {
                    context.closePopup();
                }
            });

            this.$content.find('span.close').off('click').on('click', function() {
                context.closePopup();
            });
        },


        openImagePopup: function(image, caption) {
            var context = this,
                fragment, img, p, imgLoad;

            this.closedPopup = false;
            $('body').addClass('popup-loader');

            fragment = document.createDocumentFragment();

            p = document.createElement('p');
            p.innerHTML = caption;
            fragment.appendChild(p);

            img = document.createElement('img');
            img.src = image;
            fragment.appendChild(img);

            this.$contentData.html(fragment);

            imgLoad = imagesLoaded(this.$contentData);
            imgLoad.on( 'done', function(instance) {
                if (context.closedPopup) {return false;}
                var image = instance.images[0].img;
                context.setContent(img.width + 10, img.height + 40);
                $('body').removeClass('popup-loader').addClass('popup');
                console.log('DONE  - all images have been successfully loaded');
            });

            $(window).resize(function(){
                $('.className').css({
                    position:'absolute',
                    left: ($(window).width() - $('.className').outerWidth())/2,
                    top: ($(window).height() - $('.className').outerHeight())/2
                });
            });
        },


        setContent: function(width, height) {
            var windowWidthBorders = $(window).width() - 30,
                windowHeightBorders = $(window).height() - 30;

            width = width + 20;
            height = height + 20;

            if (width > windowWidthBorders) {
                width = windowWidthBorders;
            }
            if (height > windowHeightBorders) {
                height = windowHeightBorders;
            }

            this.$content.css({
                width: width + 'px',
                height: height + 'px',
                top: '50%',
                left: '50%',
                'margin-left': width / -2  + 'px',
                'margin-top':  height / -2  + 'px'
            });

            this.$contentData.css({
                width: width + 'px',
                height: height + 'px'
            });
        },


        closePopup: function () {
            if (this.closedPopup) {return false;}
            $('body').removeClass('popup popup-loader');
            this.closedPopup = true;
        }
    })
})(jQuery, m4a)