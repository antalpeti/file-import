const UTIL = {
  scroll_two_textarea: function(id1, id2) {
    const ta1 = document.getElementById(id1);
    if(ta1 == null) {
      return console.warn("[tln.js] Couldn't find textarea of id '"+id1+"'");
    }
    const ta2 = document.getElementById(id2);
    if(ta2 == null) {
      return console.warn("[tln.js] Couldn't find textarea of id '"+id2+"'");
    }
    function select_scroll_1(e) {
        ta2.scrollTop = ta1.scrollTop;
    }
    function select_scroll_2(e) {
        ta1.scrollTop = ta2.scrollTop;
    }
    ta1.addEventListener('scroll', select_scroll_1, false);
    ta2.addEventListener('scroll', select_scroll_2, false);
  }
}