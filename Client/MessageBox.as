package  {
	
	import flash.display.MovieClip;
	import flash.events.MouseEvent;
	
	//Classe de message pop up
	public class MessageBox extends MovieClip {
		var containeur:MovieClip;
		
		public function MessageBox(containeur:MovieClip,corps:String,titre:String) {
			this.containeur = containeur;
			this.x = 365;
			this.y = 325;
			this.titre.text = titre;
			this.corps.autoSize = "left";
			this.corps.text = corps;
			this.corps.background = false;
			this.corps.border = false;
			this.titre.background = false;
			this.titre.border = false;
						
			var add:int = this.corps.textHeight - 30;
			this.fond.height += add;
			this.ok.y += add;
			this.y -= add/2;
			
			containeur.addChild(this);
			ok.addEventListener(MouseEvent.CLICK,close);
		}
		
		public function close(e:MouseEvent){
			containeur.removeChild(this);
			ok.removeEventListener(MouseEvent.CLICK,close);                                                   
		}
		
	}
	
}
