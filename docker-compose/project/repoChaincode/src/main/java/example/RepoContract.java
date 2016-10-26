/*
 *
 * Copyright DTCC 2016 All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * /
 *
 */

package example;
import  org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Created by cadmin on 7/11/16.
 */
public class RepoContract implements Serializable{
    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getXref() {
        return xref;
    }

    public void setXref(String xref) {
        this.xref = xref;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getContraID() {
        return contraID;
    }

    public void setContraID(String contraID) {
        this.contraID = contraID;
    }

    public float getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(float repoRate) {
        this.repoRate = repoRate;
    }

    public float getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(float startAmount) {
        this.startAmount = startAmount;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public RepoContract(String xref, String tid, String partID, String contraID, float repoRate, float startAmount, int par) {

        this.xref = xref;
        this.tid = tid;
        this.partID = partID;
        this.contraID = contraID;
        this.repoRate = repoRate;
        this.startAmount = startAmount;
        this.par = par;
    }

    private String xref, tid, partID, contraID, cusip;
    private float repoRate, startAmount;
    private int par;
    private boolean matched;

    public String getMatched_key() {
        return matched_key;
    }

    public void setMatched_key(String matched_key) {
        this.matched_key = matched_key;
    }

    private String matched_key;

    public boolean isMatched() {
        return matched;
    }

    public void setMatched() {
        this.matched = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepoContract that = (RepoContract) o;

        if (Float.compare(that.getRepoRate(), getRepoRate()) != 0) return false;
        if (Float.compare(that.getStartAmount(), getStartAmount()) != 0) return false;
        if (getPar() != that.getPar()) return false;
        if (!getXref().equals(that.getXref())) return false;
        if (!getTid().equals(that.getTid())) return false;
        if (!getCusip().equals(that.getCusip())) return false;
        if (!getPartID().equals(that.getPartID())) return false;
        return getContraID().equals(that.getContraID());

    }

    public String getCusip() {
        return cusip;
    }

    public void setCusip(String cusip) {
        this.cusip = cusip;
    }

    public RepoContract(String xref, String tid, String partID, String contraID, String cusip, float repoRate, float startAmount, int par) {
        this.xref = xref;
        this.tid = tid;
        this.partID = partID;
        this.contraID = contraID;
        this.cusip = cusip;
        this.repoRate = repoRate;
        this.startAmount = startAmount;
        this.par = par;
    }


    public boolean match(RepoContract o) {
        if (this == o) return true;

        RepoContract that = (RepoContract) o;

        if (Float.compare(that.getRepoRate(), getRepoRate()) != 0) return false;
        if (Float.compare(that.getStartAmount(), getStartAmount()) != 0) return false;
        if (getPar() != that.getPar()) return false;
        if (!getCusip().equals(that.getCusip())) return false;
        if (!getPartID().equals(that.getContraID())) return false;
        return getContraID().equals(that.getPartID());
    }

    @Override
    public String toString() {
        return "RepoContract{" +
                "xref='" + xref + '\'' +
                ", tid='" + tid + '\'' +
                ", partID='" + partID + '\'' +
                ", contraID='" + contraID + '\'' +
                ", cusip='" + cusip + '\'' +
                ", repoRate=" + repoRate +
                ", startAmount=" + startAmount +
                ", par=" + par +
                ", matched=" + matched +
                ", matched_key='" + matched_key + '\'' +
                '}';
    }


    public String getKey() {
        return  xref + '|' +
                tid + '|' +
                partID + '|' +
                contraID ;//+ '|' +
//                + repoRate + '|' +
//                + startAmount + '|' +
//                + par ;
    }
    public byte[] toByteArr(){
        byte[] data = SerializationUtils.serialize(this);
        return data;//byte[] this;

    }

    public static RepoContract toRepoObj(byte[] byteArr ){
        return (RepoContract) SerializationUtils.deserialize(byteArr);
    }


}
