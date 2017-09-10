package tz.co.nezatech.dev.nezahttp;

public interface HttpPostProgressListener {
	public void progressChanged(int bytesAdded, int currentProgress, int totalSize);
	public void postCompleted(int totalSize);
}
