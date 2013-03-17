package com.bpodgursky.set_query_lib;

import java.io.IOException;

public abstract class UncaughtRunnable implements Runnable {

  @Override
  public void run() {
    try{
      runInternal();
    }catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  public abstract void runInternal() throws IOException;
}
