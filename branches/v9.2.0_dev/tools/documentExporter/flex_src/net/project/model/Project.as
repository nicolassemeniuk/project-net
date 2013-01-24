package net.project.model
{
	[Bindable]
	[RemoteClass(alias="net.project.model.Project")]
	public class Project
	{
		public function Project()
		{
		}

		public var projectId:Number;
		public var parentSpaceId:Number;
		public var childSpaceId:Number;
		public var projectName:String;
		public var level:Number;
		public var path:String;
		public var selected:Boolean;
		public var folderName:String;
	
	}
}