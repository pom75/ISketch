package  {
	import flash.display.MovieClip;
	import flash.text.TextField;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import flash.ui.MouseCursor;
	import flash.events.KeyboardEvent;
	
	
	public class Options extends MovieClip{
		public var fenetre:Fenetre;
		public var menu:MenuOption;
		public var isOpen:Boolean = false;
			
		public function Options(f:Fenetre,x:int,y:int){
			this.fenetre=f;
			this.x=x;
			this.y=y;
			menu = new MenuOption(fenetre);
		}
		
		public function afficherThis(){
			fenetre.addChild(this);
			this.addEventListener(MouseEvent.CLICK,openMenu);
		}
		
		public function effacerThis(){
			this.removeEventListener(MouseEvent.CLICK,openMenu);
			fenetre.removeChild(this);
		}
		
		public function openMenu(e:MouseEvent){
				menu.afficherThis();
			
		}
		
	}
	
}