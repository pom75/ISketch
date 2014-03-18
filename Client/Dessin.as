package  {
	
	import flash.display.Sprite
	import flash.text.TextField;
	import flash.events.MouseEvent;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.display.BitmapData;
	import flash.display.Bitmap;
	import flash.display.MovieClip;
	import flash.display3D.IndexBuffer3D;
	
	public class Dessin extends MovieClip {
		var pal:Object;
		var DRAW:Object;
		var taille:int = 5;
		var fenetre:Fenetre;
		var isDess:Boolean = false;
		
		public function Dessin(fenetre:Fenetre,xx:int,yy:int) {
			this.fenetre=fenetre;
			this.x = xx;
			this.y = yy;
		}
		
		public function afficherThis(){
			fenetre.addChild(this);
			drawer(675,600, 1);
			PALETTE(2);
			//On cache les option de dessin
			with(pal){
				clip.visible=false;
			}
			with(DRAW){
				GL.visible=false;
			}
		}
		
		public function effacerThis(){
			fenetre.removeChild(this);
			fenetre.dessin=null;
		}
		
		//Menu et surface de dessin
		function drawer(SizeX:int , SizeY:int ,alph:Number=0.1 ,colors:int=0xFFFFFF ):void {	
		   DRAW={  size:1, fsize:taille, fmax:40, multy:0.5,
						color:0x000000, zone:new Sprite(), bg:new carre(SizeX,SizeY,colors,0.7), graph:new Sprite(), M:new carre(SizeX,SizeY,0x000000),
						GL:new Sprite(),
						B:new cbutton('X') ,br1:new cbutton('+'),br2:new cbutton('o'),br3:new cbutton('-'), col:colors, A:alph  };
		   with(DRAW){
			addChild(zone); zone.x=75; zone.y=0; 
			zone.addChild(bg);
			zone.addChild(graph);
			zone.addChild(M);
			graph.mask=M;
			zone.addChild(GL);GL.y=10; GL.x=-50;
			GL.addChild(B);B.x=0; B.y=0;
			GL.addChild(br1);br1.y=50;
			GL.addChild(br2);br2.y=100;
			GL.addChild(br3);br3.y=150;
			graph.mouseEnabled=false;
		  }
		}
		
		public function isDessinateur(test:Boolean){
			if(test){
				isDess = true;
				with(DRAW){
					changeSeting(5,"0x000000")
					br1.addEventListener(MouseEvent.CLICK,brushSizeMax);
					br2.addEventListener(MouseEvent.CLICK,brushSize);
					br3.addEventListener(MouseEvent.CLICK,brushSizeMin);
					zone.addEventListener(MouseEvent.MOUSE_DOWN,handleMouseDown);
					zone.addEventListener(MouseEvent.MOUSE_UP,handleMouseUp);
					B.addEventListener(MouseEvent.CLICK,deleteStage);
				}
				with(pal){
					clip.visible = true;
				}
				with(DRAW){
					GL.visible=true;
				}
			}else{
				if(isDess){
					isDess = true;
					with(DRAW){
						br1.removeEventListener(MouseEvent.CLICK,brushSizeMax);
						br2.removeEventListener(MouseEvent.CLICK,brushSize);
						br3.removeEventListener(MouseEvent.CLICK,brushSizeMin);
						zone.removeEventListener(MouseEvent.MOUSE_DOWN,handleMouseDown);
						zone.removeEventListener(MouseEvent.MOUSE_UP,handleMouseUp);
						B.removeEventListener(MouseEvent.CLICK,deleteStage);
					}
					with(pal){
						clip.visible=false;
					}
					with(DRAW){
					GL.visible=false;
					}
				}
			}
		}
		//Création de la palette
		public function PALETTE(Type:uint, Tx:int=115, Ty:int=115):void
		{
			
			pal={clip:new MovieClip(), p:new carre(30,30,0xFFFFFF,1) ,o:new cbutton('P'),C:new curs(),
						 tx:75, ty:75
						 }
			
			 with(pal){
				type=Type;
				this.addChild(clip);clip.x=0;clip.y=525;
				clip.addChild(p);p.y=-35;p.x=21;
				clip.addChild(o);o.y=-60;o.x=26;
				o.addEventListener('click',change_palette);
				var B:Sprite;
			 }
			 switch( Type ){
				case 1:
					var nRadius:Number =pal.tx>>1;
					var R:PALrond = new PALrond(nRadius);
					B=new Sprite();
					B.addChild(R)
					R.x = R.y = nRadius;
					vectorTObitmap(B);
				break; 
				case 2:
					var N:PALcarre = new PALcarre(pal.tx, pal.ty);
					B=new Sprite();
					B.addChild(N);
					vectorTObitmap(B);
				break; 
				}
				
		}
		
		function vectorTObitmap(map):void {  
			pal['bmp']=new BitmapData(pal.tx, pal.ty, false, 0x000000);
			pal['T'] =new Bitmap();
			pal['BMP'] = new carre(pal.tx,pal.ty,0x00,1);
			with(pal){
				bmp.draw(map,null,null,null,null,true);
				T=new Bitmap(bmp);
				clip.addChild(BMP);
				clip.addChild(T);
				clip.mouseEnabled=false;
				clip.addChild(C)
				BMP.addEventListener('click',Getcolor)
				}
		}
		
		function Getcolor(e):void {  
		//Recupere la couleur choisis et la convertie en Hexa
		var pixelValue:uint = pal.bmp.getPixel(pal.T.mouseX, pal.T.mouseY);
		var color='0x'+pixelValue.toString(16);
		//Change la couleur du pinceau
		DRAW.color=color;
		//Change la position du curseur
		pal.C.x=pal.T.mouseX;
		pal.C.y=pal.T.mouseY;
		//Change la couleur du carré 
		TweenMax.to(pal.p, .3, {colorMatrixFilter:{colorize:color}});
		//Envois de la nouvelle couleur au serveur
		InterfaceSock.changeCouleur(color);
		
		}
		//Changement de la palette 
		function change_palette(map):void { 
			with(pal){
				del_palette();
				if(type==1)PALETTE(2)
				else PALETTE(1)
				}
		}
		//Supression de la palette
		function del_palette():void {  
			with(pal){
				clip.removeEventListener('click',Getcolor);
				clip.removeChild(T);
				removeChild(clip);
			}
		}
		
		//Lorsque l'on clik sur la surface de dessin
		function handleMouseDown(event:MouseEvent):void {	
		  with(DRAW){
			size=fsize;
			//On envois les coordonné au serveur
			InterfaceSock.x1 = zone.mouseX;
			InterfaceSock.y1 = zone.mouseY;
			  
			graph.graphics.lineStyle(undefined);
			
			graph.graphics.moveTo(InterfaceSock.x1,InterfaceSock.y1);
			
			zone.addEventListener(MouseEvent.MOUSE_MOVE,startDrawing);
			zone.addEventListener(MouseEvent.MOUSE_OUT,handleMouseUp);
			TweenMax.to(DRAW, 1, {  onUpdate:upSize});
			
			}
		}
		
		//Lorsque le click de souris est levé 
		function handleMouseUp(event:MouseEvent):void {
			TweenMax.to(DRAW, 0, {});
			DRAW.zone.removeEventListener(MouseEvent.MOUSE_MOVE,startDrawing);
		}
		
		//On dessine sur la surface de dessin
		function startDrawing(event:MouseEvent):void {	
			 with(DRAW){
				var x2:int = zone.mouseX, y2:int = zone.mouseY;
				//On envois les coordonné au serveur
				InterfaceSock.traceTrait(x2,y2);
				graph.graphics.lineTo(x2,y2);
			}
		}
		//Efface la surface de dessin (interne)
		function deleteStage(event:MouseEvent):void {
			with(DRAW)graph.graphics.clear();
			InterfaceSock.clearD();
		}
		
		//Efface la surface de dessin (externe)
		function deleteStageE():void {
			with(DRAW)graph.graphics.clear();
		}
		
		//MAJ de la taille du crayon
		function upSize():void {
		   with(DRAW){
			if(size<=fmax+.3)
			graph.graphics.lineStyle(size,color);
			}
		}
		//Remet de la taille du crayon par defaut
		function brushSize(event:MouseEvent):void {
			with(DRAW){fsize=5; fmax=40; taille =5;InterfaceSock.tailleTrait(fsize);};
		}
		//Diminue de la taille du crayon min 1
		function brushSizeMin(event:MouseEvent):void {
			with(DRAW){
				if(taille == 1){
					fsize=taille; fmax=5;
				}else{
					fsize=taille--; fmax=5;
					InterfaceSock.tailleTrait(fsize);
				}
			};
		}
		//Augmente de la taille du crayon max 25
		function brushSizeMax(event:MouseEvent):void {
			with(DRAW){
				if(taille == 25){
					fsize=taille; fmax=100;
				}else{
					fsize=taille++; fmax=100;
					InterfaceSock.tailleTrait(fsize);
				}				
			};
		}
		
		
		
		//Change la couleur et la taille a partir des données serveur
		function changeSeting(s:int,col:String){
			DRAW.color=col;
			TweenMax.to(pal.p, .3, {colorMatrixFilter:{colorize:col}});
			with(DRAW){
				fsize=s;
				taille = s;
				size = s;
				graph.graphics.lineStyle(s,color);
			}
			
		}
		
		//Dessine des trait a partir des données serveur
		function dessinExt(x1:int,y1:int,x2:int,y2:int):void {
			 with(DRAW){
				size=fsize;
				TweenMax.to(DRAW, 1, {  onUpdate:upSize});
				graph.graphics.moveTo(x1,y1);
				graph.graphics.lineTo(x2,y2);
			}
		}
		
		
	}
	
}