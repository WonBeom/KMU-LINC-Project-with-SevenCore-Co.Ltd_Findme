package ips.project.graduate.findme;

import ips.project.graduate.findme.IRemoteServiceCallback;

interface IRemoteService {
	boolean registerCallback(IRemoteServiceCallback callback);
	boolean unregisterCallback(IRemoteServiceCallback callback);
}