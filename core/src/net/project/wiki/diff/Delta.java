package net.project.wiki.diff;

public class Delta{
  protected Chunk originalChunk,revisedChunk;

  public Delta(Chunk originalChunk, Chunk revisedChunk){
    this.originalChunk = originalChunk;
    this.revisedChunk = revisedChunk;
  }

  public Chunk getOriginal(){
    return originalChunk;
  }

  public Chunk getRevised(){
    return revisedChunk;
  }
}
