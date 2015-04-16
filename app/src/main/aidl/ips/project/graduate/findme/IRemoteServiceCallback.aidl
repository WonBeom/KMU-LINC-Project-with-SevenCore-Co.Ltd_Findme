package ips.project.graduate.findme;

import ips.project.graduate.findme.MapElements;

oneway interface IRemoteServiceCallback {
	void valueChanged(inout MapElements elem);
}
