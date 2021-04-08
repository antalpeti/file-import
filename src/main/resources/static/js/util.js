const UTIL = {
  synchronize_scrolling_bars: function(id1, id2) {
    const s1 = document.getElementById(id1);
    if(s1 == null) {
      return console.warn("[tln.js] Couldn't find element of id '"+id1+"'");
    }
    const s2 = document.getElementById(id2);
    if(s2 == null) {
      return console.warn("[tln.js] Couldn't find element of id '"+id2+"'");
    }
    function select_scroll_1(e) {
        s2.scrollTop = s1.scrollTop;
        s2.scrollLeft = s1.scrollLeft;
    }
    function select_scroll_2(e) {
        s1.scrollTop = s2.scrollTop;
        s1.scrollLeft = s2.scrollLeft;
    }
    s1.addEventListener('scroll', select_scroll_1, false);
    s2.addEventListener('scroll', select_scroll_2, false);
  }
}