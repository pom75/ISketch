package {
	import flash.display.Sprite;
	public class carre extends Sprite {
		public function carre(w:uint, h:uint, c:uint,a:Number=1) {
			draw(w,h,c,a);}
	private function draw(w:uint, h:uint, c:uint, a:Number):void {
			graphics.beginFill(c,a);
			graphics.drawRect(0,0,w,h);
			graphics.endFill();}
	}
}