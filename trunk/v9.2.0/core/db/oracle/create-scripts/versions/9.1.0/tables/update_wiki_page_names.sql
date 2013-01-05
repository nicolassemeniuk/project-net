/*
* Updating wiki page names by removing special characters
*/
declare
  changed_page_name pn_wiki_page.page_name%type;	
  page_counter number;
  max_value number;
    
  -- Get wiki page details which having special characters in page name
  cursor source_cursor is
   select
   w.wiki_page_id,
   w.page_name,
   w.owner_object_id
   from pn_wiki_page w
   where regexp_instr(page_name, '[[~!@#$%^&*(),`/|;+=<>{}?\"&]') != 0
   or page_name like '%]%';
begin
  for rec in source_cursor loop
    begin
      page_counter := 0;
      max_value := 0;
      
      -- Changed page name by removing special characters from page name
      changed_page_name := regexp_replace(regexp_replace(rec.page_name, '[~!@#$%^&*(),`/|;+=<>{}?\"&]', ''), '[][]', '');
      
      -- Counter to count pages having changed page name
      select count(*) into page_counter from pn_wiki_page
      where page_name = changed_page_name
      and owner_object_id = rec.owner_object_id;
      
      -- Get max value among end digit of wiki page if that page is already present
      if page_counter > 0 then
        select max(to_number(substr(w.page_name, instr(w.page_name, '_', -1) +1))) into max_value from pn_wiki_page w
        where REGEXP_LIKE(w.page_name, '_[[:digit:]]$')
        and w.page_name like changed_page_name || '%'
        and w.owner_object_id = rec.owner_object_id;
      end if;
      
      if page_counter > 0 then
		-- Set page counter if page with changed page name already exist	    
        if max_value is null then
          page_counter := 1;
        elsif max_value = 0 then
          page_counter := page_counter + 1;
        else
          page_counter := page_counter + 2;
        end if;
        
        -- Update pn_wiki_page table with changed page name and ending with page counter
        update pn_wiki_page 
        set page_name = changed_page_name || '_' || to_char(page_counter-1)
        where wiki_page_id = rec.wiki_page_id;
		
      else
        -- Update pn_wiki_page table with changed page name without page counter
        update pn_wiki_page 
        set page_name = changed_page_name
        where wiki_page_id = rec.wiki_page_id;
      end if;
	  
	  -- Commit changes to get reflected for next pages
      commit;
	  
      exception
	     when others then
			 begin
				dbms_output.put_line('Error occurred while updating wiki page :-' || rec.wiki_page_id );
			 end;
   end;
  end loop;
end;
/