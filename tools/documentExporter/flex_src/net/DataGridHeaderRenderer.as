package net{
	import mx.containers.Box;

	public class DataGridHeaderRenderer extends Box{
		
		public function DataGridHeaderRenderer(){
			this.graphics.lineStyle(4, 0x0000ff);
			this.graphics.moveTo(0,0);
			this.graphics.lineTo(0, this.width);
		}
		
		
		
	}
}