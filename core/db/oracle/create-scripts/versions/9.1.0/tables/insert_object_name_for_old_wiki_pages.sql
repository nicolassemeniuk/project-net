-- insert old wiki page id and page name to pn_object_name table
declare
  pageName pn_wiki_page.page_name%type;
  cursor wikiIds is
        select pw.wiki_page_id from pn_wiki_page pw
        where pw.wiki_page_id not in (select object_id from pn_object_name);
begin
   for rec in wikiIds loop
	 begin
		  select pw.page_name into pageName from pn_wiki_page pw where pw.wiki_page_id = rec.wiki_page_id;
		  insert into pn_object_name values (rec.wiki_page_id, pageName);
		  exception
		  when others then
			begin
				dbms_output.put_line('Error occurred while inserting wiki page names in pn_object_name table -'|| rec.wiki_page_id);            
			end;
	 end; 
   end loop;
end;
/