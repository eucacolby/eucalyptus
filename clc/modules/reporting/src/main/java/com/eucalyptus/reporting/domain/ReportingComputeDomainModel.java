/*************************************************************************
 * Copyright 2009-2012 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 *
 * This file may incorporate work covered under the following copyright
 * and permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms,
 *   with or without modification, are permitted provided that the
 *   following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 *   THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 *   COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 *   AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 *   SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 *   WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 *   REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 *   IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 *   NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/
package com.eucalyptus.reporting.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ReportingComputeDomainModel contains statistics of the total compute resources available,
 * both for the entire cloud and for various availability zones. It can be used to display reports
 * of how many compute resources are available. It is unlike the other reporting functionality
 * insofar as it does not contain any historical information or store any events. As a result it is
 * not found in the event_store package.
 * 
 * <p>ReportingComputeDomainModel has static methods for accessing the statistics of the entire
 * cloud (<code>getGlobalComputeDomainModel()</code>) and for accessing the statistics of a
 * particular availability zone (<code>getZoneComputerDomainModel(zoneName)</code>). In both cases,
 * some external agent calls these static methods to obtain domain model objects and then populates
 * their properties with values based upon the amount of hardware available.
 * 
 * <p>ReportingComputeDomainModel contains a property of how many compute resources are available.
 * This property indicates how many instances could be run, based upon the number of cores. It is a
 * <i>multiplier</i> and you can determine how many instances of various types you can create by
 * dividing it by some integer.
 * 
 */
public class ReportingComputeDomainModel
{
	private static ReportingComputeDomainModel globalModel = null;
	private static Map<String, ReportingComputeDomainModel> zoneModels; 
	
	public static ReportingComputeDomainModel getGlobalComputeDomainModel()
	{
		if (globalModel == null) {
			globalModel = new ReportingComputeDomainModel();
		}
		return globalModel;
	}
	
	public static ReportingComputeDomainModel getZoneComputeDomainModel(String zoneName)
	{
		if (zoneModels == null) {
			zoneModels = new HashMap<String,ReportingComputeDomainModel>();
		}
		
		if (! zoneModels.containsKey(zoneName) ) {
			zoneModels.put(zoneName, new ReportingComputeDomainModel());
		}
		
		return zoneModels.get(zoneName);
	}
	
	private ReportingComputeDomainModel()
	{
		ec2ComputeUnitsAvailable = null;
		sizeS3ObjectAvailableGB      = null;
		sizeEbsAvailableGB           = null;
		numPublicIpsAvailable        = null;
	}
	
	private Integer ec2ComputeUnitsAvailable;
	private Integer ec2MemoryUnitsAvailable;
	private Integer ec2DiskUnitsAvailable;
	private Long    sizeS3ObjectAvailableGB;
	private Long    sizeEbsAvailableGB;
	private Integer numPublicIpsAvailable;
	

	/**
	 * <p>Returns the total number of instances which can be created, based upon the amount of
	 * 	compute resources available in the cloud. This value is a <i>multiplier</i>, and you
	 *  can determine how many instances of various types you can create by dividing it by some
	 *  number, as follows:
	 *  
	 *  <p>number of m1.small  instances : divide by 1.
	 *  <p>number of m1.medium instances : divide by 2.
	 *  <p>number of c1.medium instances : divide by 5.
	 *  <p>number of m1.large  instances : divide by 4.
	 *  <p>number of m1.xlarge instances : divide by 8.
	 *  <p>number of c1.xlarge instances : divide by 20.
	 *
	 * See: http://aws.amazon.com/ec2/instance-types/
	 *  
	 * @return Total number of ec2 compute units available. 
	 */
	public Integer getEc2ComputeUnitsAvailable()
	{
		return ec2ComputeUnitsAvailable;
	}

	/**
	 * <p>The total number of instances which can be created, based upon the amount of
	 * 	compute resources available in the cloud. This value is a <i>multiplier</i>, and you
	 *  can determine how many instances of various types you can create by dividing it by some
	 *  number, as follows:
	 *  
	 *  <p>number of m1.small  instances : divide by 1.
	 *  <p>number of m1.medium instances : divide by 2.
	 *  <p>number of c1.medium instances : divide by 5.
	 *  <p>number of m1.large  instances : divide by 4.
	 *  <p>number of m1.xlarge instances : divide by 8.
	 *  <p>number of c1.xlarge instances : divide by 20.
	 *
	 * See: http://aws.amazon.com/ec2/instance-types/
	 */
	public void setEc2ComputeUnitsAvailable(Integer numM1SmallInstancesAvailable)
	{
		this.ec2ComputeUnitsAvailable = numM1SmallInstancesAvailable;
	}
	
	/**
	 * <p>The total number of instances of various types which could be created based upon the
	 *  the amount of RAM available in the cloud and its distribution across nodes. This value is
	 *  a <i>multiplier</i>, and you can determine how many instances of various types you can
	 *  create by dividing it by some number, as follows:
	 *  
	 *  <p>number of m1.small  instances : divide by 1.
	 *  <p>number of m1.medium instances : divide by 2.
	 *  <p>number of c1.medium instances : divide by 1.
	 *  <p>number of m1.large  instances : divide by 4.
	 *  <p>number of m1.xlarge instances : divide by 8.
	 *  <p>number of c1.xlarge instances : divide by 4.
	 * 
	 */
	public Integer getEc2MemoryUnitsAvailable()
	{
		return ec2MemoryUnitsAvailable;
	}

	public void setEc2MemoryUnitsAvailable(Integer ec2MemoryUnitsAvailable)
	{
		this.ec2MemoryUnitsAvailable = ec2MemoryUnitsAvailable;
	}

	/**
	 * <p>The total number of instances of various types which could be created based upon the
	 *  the amount of disk available in the cloud and its distribution across nodes. This value is
	 *  a <i>multiplier</i>, and you can determine how many instances of various types you can
	 *  create by dividing it by some number, as follows:
	 *  
	 *  <p>number of m1.small  instances : divide by 1.
	 *  <p>number of m1.medium instances : divide by 2.
	 *  <p>number of c1.medium instances : divide by 2.
	 *  <p>number of m1.large  instances : divide by 4.
	 *  <p>number of m1.xlarge instances : divide by 8.
	 *  <p>number of c1.xlarge instances : divide by 8.
	 * 
	 */
	public Integer getEc2DiskUnitsAvailable() {
		return ec2DiskUnitsAvailable;
	}

	public void setEc2DiskUnitsAvailable(Integer ec2DiskUnitsAvailable) {
		this.ec2DiskUnitsAvailable = ec2DiskUnitsAvailable;
	}

	public Long getSizeS3ObjectAvailableGB()
	{
		return sizeS3ObjectAvailableGB;
	}

	public void setSizeS3ObjectAvailableGB(Long sizeS3ObjectAvailableGB)
	{
		this.sizeS3ObjectAvailableGB = sizeS3ObjectAvailableGB;
	}

	public Long getSizeEbsAvailableGB()
	{
		return sizeEbsAvailableGB;
	}

	public void setSizeEbsAvailableGB(Long sizeEbsAvailableGB)
	{
		this.sizeEbsAvailableGB = sizeEbsAvailableGB;
	}

	public Integer getNumPublicIpsAvailable()
	{
		return numPublicIpsAvailable;
	}

	public void setNumPublicIpsAvailable(Integer numPublicIpsAvailable)
	{
		this.numPublicIpsAvailable = numPublicIpsAvailable;
	}


}