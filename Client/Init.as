package  {
	
	import flash.display.MovieClip;
	import flash.events.MouseEvent;
	
	
	public class Init extends MovieClip {
		
		public var scene:MovieClip ;
		
		public function Init(scene:MovieClip) {
			this.scene=scene;
			this.send.addEventListener(MouseEvent.CLICK, envois);
			this.scene.addChild(this);
			this.x = 300;
			this.y = 300;
		}
		
		public function envois(e:MouseEvent):void
		{
			scene.HostIp = this.ip.text;
			scene.HostPort = Number(this.port.text);
			scene.gotoAndPlay(2);
		}
		
		
	}
	
}
