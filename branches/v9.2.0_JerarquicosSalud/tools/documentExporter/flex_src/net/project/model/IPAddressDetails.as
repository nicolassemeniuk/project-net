package net.project.model
{
	[Bindable]
	[RemoteClass(alias="net.project.dto.IPAddressDetails")]
	public class IPAddressDetails
	{
		public function IPAddressDetails()
		{
		}

	public var ipAddress:String;

	public var latitude:String;
	
	public var longitude:String;
	
	public var country:String;
	
	public var area:String;
	
	public var city:String;
	
	public var organization:String;
	}
}