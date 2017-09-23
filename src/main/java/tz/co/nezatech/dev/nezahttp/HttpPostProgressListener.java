package tz.co.nezatech.dev.nezahttp;

public interface HttpPostProgressListener {
	public void progressChanged(long bytesAdded, long currentProgress, long totalSize);
	public void postCompleted(long totalSize);
}
