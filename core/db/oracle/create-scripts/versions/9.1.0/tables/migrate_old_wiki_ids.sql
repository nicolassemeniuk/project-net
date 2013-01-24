/*
* Updating the wiki created by id with the edited by id to avoid null values for existing records
*/

begin 
  for x in ( select * from pn_wiki_page) loop
    begin
       update pn_wiki_page set created_by = x.edited_by,
       created_date = x.edit_date
       where wiki_page_id = x.wiki_page_id;
       exception
          WHEN OTHERS THEN
          dbms_output.put_line('Error while update .');
    end;
  end loop;
end;
/

/*
* Updating the parent ids of the existing parent ids to root page id
*/

declare
    cursor source_cursor is
    select distinct owner_object_id from pn_wiki_page;
begin
    for rec in source_cursor loop
      begin
       update pn_wiki_page
       set parent_page_id = (select wiki_page_id from pn_wiki_page where owner_object_id = rec.owner_object_id and parent_page_id is null and record_status = 'A')
       where parent_page_id is not null and owner_object_id = rec.owner_object_id and record_status = 'A';
       exception
         when others then
        dbms_output.put_line('Error:-' || rec.owner_object_id );           
      end;
    end loop;
end;
/

/*
* Updating the wiki page id with the object id
*/

-- disabling the foriegn key constraint
alter table PN_WIKI_PAGE disable constraint FK_PN_WIKI_PAGE_PARENT_ID;
alter table PN_WIKI_ATTACHMENT disable constraint WIKI_ATTACHMENT_WP_ID_FK;
alter table PN_WIKI_HISTORY disable constraint WIKI_HISTORY_PAGE_ID_FK;
alter table PN_WIKI_ASSIGNMENT disable constraint PN_WIKI_PAGE_ID_FK;
/

declare
  uncommitted_count number(20) := 0;  
  new_wiki_page_id number(20) := 0;  
  cursor source_cursor is
    SELECT 
      W.WIKI_PAGE_ID,
      W.CREATED_BY,
      W.OWNER_OBJECT_ID
    FROM
      PN_WIKI_PAGE W; 
begin
    
    for rec in source_cursor loop
      begin
        
      -- create new blog object
      new_wiki_page_id := BASE.CREATE_OBJECT('wiki', rec.created_by, 'A');

      -- update the PN_WIKI_PAGE table for new object_id as parent wiki_page_id
      update PN_WIKI_PAGE w
      set w.parent_page_id = new_wiki_page_id
      where w.parent_page_id = rec.wiki_page_id;
    
      -- update the PN_WIKI_PAGE table for new object_id as wiki_page__id
      update PN_WIKI_PAGE w
      set w.wiki_page_id = new_wiki_page_id
      where w.wiki_page_id = rec.wiki_page_id;
    
      -- update the PN_WIKI_ATTACHMENT table for new object_id as wiki_page_id
      update PN_WIKI_ATTACHMENT wa
      set wa.wiki_page_id = new_wiki_page_id
      where wa.wiki_page_id = rec.wiki_page_id;

      -- update the PN_WIKI_HISTRORY table for new object_id as wiki_page_id
      update PN_WIKI_HISTORY wh
      set wh.wiki_page_id = new_wiki_page_id
      where wh.wiki_page_id = rec.wiki_page_id;

      -- update the PN_WIKI_ASSIGNMENT table for new object_id as wiki_page_id
      update PN_WIKI_ASSIGNMENT pwa
      set pwa.wiki_page_id = new_wiki_page_id
      where pwa.wiki_page_id = rec.wiki_page_id;

      exception
          when others then 
           null;
      end;   
     
      uncommitted_count := uncommitted_count + 1;
        
        --Commit every 10th insert to make sure we don't overflow
        --rollback log.
        if (uncommitted_count >=10) then
            commit;
            uncommitted_count := 0;
        end if;
    end loop;
      
    --Commit any remaining insertions
    commit;
exception
    when others then
        begin
            dbms_output.put_line('Error occurred while updating wiki object id.');            
        end;
end;
/

-- enabling the foriegn key constraint
alter table PN_WIKI_PAGE enable constraint FK_PN_WIKI_PAGE_PARENT_ID;
alter table PN_WIKI_ATTACHMENT enable constraint WIKI_ATTACHMENT_WP_ID_FK;
alter table PN_WIKI_HISTORY enable constraint WIKI_HISTORY_PAGE_ID_FK;
alter table PN_WIKI_ASSIGNMENT enable constraint PN_WIKI_PAGE_ID_FK;
/