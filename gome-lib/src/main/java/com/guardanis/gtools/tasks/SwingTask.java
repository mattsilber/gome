package com.guardanis.gtools.tasks;

import javax.swing.SwingWorker;

import com.guardanis.gtools.general.Callback;

public abstract class SwingTask<T> extends SwingWorker<T, Void> implements Callback<T> {
		
	protected Callback<T> completionCallback;
	protected Callback<Double> progressCallback;
	protected Callback<Throwable> errorCallback;
	
	protected T result;
	
	private boolean started = false;

	public SwingTask<T> setCompletionCallback(Callback<T> completionCallback){
		this.completionCallback = completionCallback;
		return this;
	}
	
	public SwingTask<T> setProgressCallback(Callback<Double> progressCallback){
		this.progressCallback = progressCallback;
		return this;
	}
	
	public SwingTask<T> setErrorCallback(Callback<Throwable> errorCallback){
		this.errorCallback = errorCallback;
		return this;
	}
	
	public SwingTask<T> start(){
		if(result != null || started)
			throw new IllegalStateException("Task is either running or finished!");
				
		execute();
		
		return this;
	}
		
	@Override
	public void done(){
		if(result == null){
			if(errorCallback != null)
				errorCallback.onCalled(new RuntimeException("Request returned a null result!"));
		}
		else if(completionCallback != null)
			completionCallback.onCalled(result);

		onDestroyResources();
	}

	@Override
	public void onCalled(T value) {
		this.result = value;	
	}
	
	protected void onDestroyResources(){				
		System.gc();
	}
	
}