package {
	import flash.display.CapsStyle;
	import flash.display.GradientType;
	import flash.display.LineScaleMode;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	//Classe importé palette carré
	public class PALcarre extends Sprite{
		public function PALcarre(W:Number = 100, H:Number = 100)
		{
			INIT(W, H);
		}
		public function INIT(W:Number = 100, H:Number = 100):void
		{
			var Perc : Number;
			var rad: Number;
			var nR: Number;
			var nG: Number;
			var nB: Number;
			var nC: Number;
			var matrixx: Matrix;
			var matrix: Matrix;
			var max: Number;
			graphics.clear();
			max = H * 0.5;
			for(var i:int = 0; i < W; i++)
			{
				Perc = i / W;
				rad = (-360 * Perc) * (Math.PI / 180);
				nR = Math.cos(rad)                   * 127 + 128 << 16;
				nG = Math.cos(rad + 2 * Math.PI / 3) * 127 + 128 << 8;
				nB = Math.cos(rad + 4 * Math.PI / 3) * 127 + 128;
				nC  = nR | nG | nB;
				matrixx = new Matrix();
				matrixx.createGradientBox(1, max, Math.PI * 0.5, 0, 0);
				matrix = new Matrix();
				matrix.createGradientBox(1, max, Math.PI * 0.5, 0, max);
				graphics.lineStyle(1, 0, 1, false, LineScaleMode.NONE, CapsStyle.NONE);
				graphics.lineGradientStyle(GradientType.LINEAR, [0xFFFFFF, nC], [100, 100], [0, 255], matrixx);
				graphics.moveTo(i, 0);
				graphics.lineTo(i, max);
				graphics.lineGradientStyle(GradientType.LINEAR, [nC, 0], [100, 100], [0, 255], matrix);
				graphics.moveTo(i, max);
				graphics.lineTo(i, H);
			}
		}
	}
}