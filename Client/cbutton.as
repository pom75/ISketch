package {
    import flash.display.*;
   // import flash.text.StyleSheet;
    /*bouton cubique*/ 
    public class cbutton extends SimpleButton
    {
        public var selected:Boolean = false;

        public function cbutton(text:String,  pwidth:int = 20, pheight:int = 20)
        {
            upState = new ButtonState(pwidth, pheight, "<p align='center'><font face='verdana' color='#CCCCCC'><b>"+text+"</b></font></p>", 0x666666, .6);
            overState = new ButtonState( pwidth, pheight, "<p align='center'><font face='verdana' color='#FFFFFF'><b>"+text+"</b></font></p>", 0xFF6600, 1);
            downState = new ButtonState( pwidth, pheight, "<p align='center'><font face='verdana' color='#000000'><b>"+text+"</b></font></p>", 0xFF0000, 1);
            hitTestState = new ButtonState( pwidth, pheight);
        }
    }
}
import flash.text.*;
import flash.display.*;
import carre;

class ButtonState extends Sprite
{
    public function ButtonState( pwidth:int, pheight:int, text:String = null, C:uint=0x00, A:Number=1)
    {
        addChild(new carre(pwidth, pheight, C , A));
        if (text)
        {  var label:TextField = new TextField();
            label.width=pwidth;
            label.htmlText = text;
            addChild(label);
        }
    }
}
