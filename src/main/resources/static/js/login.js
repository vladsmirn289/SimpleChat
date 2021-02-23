$(function () {
    $(".form").find('input, textarea').on('keyup blur focus', function (e) {
        let $this = $(this),
            label = $this.prev('label');

        if (e.type === 'keyup') {
            if ($this.val() === '') {
                label.removeClass('active highlight');
            } else {
                label.addClass('active highlight');
            }
        } else if (e.type === 'blur') {
            if( $this.val() === '' ) {
                label.removeClass('active highlight');
            } else {
                label.removeClass('highlight');
            }
        } else if (e.type === 'focus') {

            if( $this.val() === '' ) {
                label.removeClass('highlight');
            }
            else if( $this.val() !== '' ) {
                label.addClass('highlight');
            }
        }

    });

    $(".tab a").on('click', function (e) {
        e.preventDefault();

        $(this).parent().addClass('active');
        $(this).parent().siblings().removeClass('active');

        let target = $(this).attr('href');
        $('.tab-content > div').not(target).hide();
        $('.tab-content > div').not($('.tab-content > div').not(target)).show();

        $(target).fadeIn(600);
    });

    let tab = document.location.hash;
    let search = document.location.search;
    if (tab === '#login_tab' || search === '?error') {
        $('.tab a').first().parent().removeClass('active');
        $('.tab a').last().parent().addClass('active');
        $('#signup').hide();
        $('#login_tab').show();
    } else if (tab === '#signup') {
        $('.tab a').first().parent().addClass('active');
        $('.tab a').last().parent().removeClass('active');
        $('#login_tab').hide()
        $('#signup').show();
    }
})