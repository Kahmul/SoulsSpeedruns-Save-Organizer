package com.soulsspeedruns.organizer.games.ui;


public interface GameListListener
{

	public void entryCreated(GameListEntry entry);

	public void entryUpdated(GameListEntry entry);

	public void entryDeleted(GameListEntry entry);
	
	public void entrySelected(GameListEntry prevEntry, GameListEntry newEntry);

}
