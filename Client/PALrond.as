package {
	import flash.display.CapsStyle;
	import flash.display.GradientType;
	import flash.display.LineScaleMode;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	//CLasse importé
	public class PALrond extends Sprite{
		
		public function PALrond(R:Number = 100)
		{
			INIT(R);
		}
		public function INIT(R:Number = 100):void
		{
			var nrad: Number;
			var nR: Number;
			var nG: Number;
			var nB: Number;
			var nC: Number;
			var matrix: Matrix;
			var nX: Number;
			var nY: Number;
			var iT : int;
			graphics.clear();
			iT = 1 + int(R / 50);
			for(var i:int = 0; i < 360; i++)
			{
				nrad = i * (Math.PI / 180);
				nR = Math.cos(nrad)* 127 + 128 << 16;
				nG = Math.cos(nrad + 2 * Math.PI / 3) * 127 + 128 << 8;
				nB = Math.cos(nrad + 4 * Math.PI / 3) * 127 + 128;
				nC = nR | nG | nB;
				nX = R * Math.cos(nrad);
				nY = R * Math.sin(nrad);
				matrix = new Matrix();
				matrix.createGradientBox(R * 2, R * 2, nrad, -R, -R);
				graphics.lineStyle(iT, 0, 1, false, LineScaleMode.NONE, CapsStyle.NONE);
				graphics.lineGradientStyle(GradientType.LINEAR, [0xFFFFFF, nC], [100, 100], [127, 255], matrix);
				graphics.moveTo(0, 0);
				graphics.lineTo(nX, nY);
			}
		}
	}
}