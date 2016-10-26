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

 import com.google.protobuf.ByteString;
 import org.apache.commons.lang3.SerializationException;
 import org.apache.commons.logging.Log;
 import org.apache.commons.logging.LogFactory;
 import org.hyperledger.java.shim.ChaincodeBase;
 import org.hyperledger.java.shim.ChaincodeStub;

 import java.util.Map;

/**
 * Created by cadmin on 7/12/16.
 */
public class RepoChain extends ChaincodeBase {
    private static Log log = LogFactory.getLog(RepoChain.class);

    @Override
    public String run(ChaincodeStub stub, String function, String[] args) {

        log.info("Called run with function arg -" + function);
        switch (function) {
            case "init":
                log.info("Inside init");
                init(stub, function, args);

                break;
            case "instruct":
                if (args.length < 8) {
                    log.error("Not enough args for instruct, return error");
                    return "Error: Not enough args for instruct, return error";
                } else {
                    log.info("Trade instruct");
                    String xref, tid, partID, contraID, cusip;
                    float repoRate, startAmount;
                    int par;
                    xref = args[0];
                    tid = args[1];
                    partID = args[2];
                    contraID = args[3];
                    repoRate = Float.parseFloat(args[4]);
                    startAmount = Float.parseFloat(args[5]);
                    par = Integer.parseInt(args[6]);
                    cusip = args[7];
                    RepoContract repoObj = new RepoContract(xref, tid, partID, contraID, cusip, repoRate, startAmount, par);
                    log.info("Trade instruct with -" + repoObj);
                    stub.putRawState("~" + repoObj.getKey(), ByteString.copyFrom(repoObj.toByteArr()));
                    matchRepo(stub, repoObj);
                }
                break;
            case "deltrades":
                Map<String, ByteString> allTrades = null;
                // query for only unmatched trades identified by the ~ prefix in the key
                allTrades = stub.rangeQueryRawState("", "");
                for (String key:allTrades.keySet()) {
                    stub.delState(key);
                }
                break;


        }
        return null;
    }

    private static String getKeyFromArgs(String[] args) {
        return args[0] + '|' + args[1] + '|' + args[2] + '|' + args[3];
    }

    private void matchRepo(ChaincodeStub stub, RepoContract newTrade) {
        Map<String, ByteString> unMatchedTrades = null;
        // query for only unmatched trades identified by the ~ prefix in the key
        unMatchedTrades = stub.rangeQueryRawState("~", "");
        log.info("Inside match trade for trade " + newTrade.toString());

        for (Map.Entry<String, ByteString> repoIter : unMatchedTrades.entrySet()) {
            RepoContract iterTrade = RepoContract.toRepoObj(repoIter.getValue().toByteArray());
            if  (!iterTrade.isMatched() && newTrade.match(iterTrade)) {
                log.info("Matched trade with key " + newTrade.getKey() + "-" + iterTrade.getKey());
                iterTrade.setMatched();
                newTrade.setMatched();
                iterTrade.setMatched_key(newTrade.getKey());
                newTrade.setMatched_key(iterTrade.getKey());
                stub.delState(repoIter.getKey());
                stub.putRawState(iterTrade.getKey(), ByteString.copyFrom(iterTrade.toByteArr()));
                stub.delState("~" + newTrade.getKey());
                stub.putRawState(newTrade.getKey(), ByteString.copyFrom(newTrade.toByteArr()));
            }

        }
    }

    @Override
    public String query(ChaincodeStub stub, String function, String[] args) {
        String returnString;
        switch (function) {
            case "query":
                if (args.length < 5) {
                    returnString = "Incorrect query parameters";
                } else {
                    String reqkey = (args[4].equalsIgnoreCase("M") ? "" : "~") + getKeyFromArgs(args);
                    log.info("Querying ledger with key - " + reqkey);
                    RepoContract repoObj = RepoContract.toRepoObj(stub.getRawState(reqkey).toByteArray());
                    log.info("Returned repoObj - " + repoObj);
                    returnString = repoObj.toString();
                }
                break;
            case "keys":
                returnString = stub.rangeQueryState(args[0], args[1]).keySet().toString();
                break;
            case "trades":
                log.info("Inside all trades query");
                Map<String, ByteString> allTrades = null;
                StringBuffer sb = new StringBuffer("");

                try {
                    allTrades = stub.rangeQueryRawState("", "");
                    for (Map.Entry<String, ByteString> repoIter : allTrades.entrySet()) {
                        RepoContract iterTrade = null;
                        try {
                            iterTrade = RepoContract.toRepoObj(repoIter.getValue().toByteArray());
                        } catch (SerializationException e) {
                            log.info("Failed to deserialize for key " + repoIter.getKey());
                            continue;
                        }
                        sb.append("==> "+iterTrade.toString() + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info(e.getMessage());
                }
                log.info(sb.toString());
                returnString = sb.toString();
                break;

            default:
                returnString = "Invalid function input";
        }
        return returnString;
    }

    public String init(ChaincodeStub stub, String function, String[] args) {
        //stub.putState("init", "init");
        log.info("Inside init");
        return null;
    }

    @Override
    public String getChaincodeID() {
        return "RepoChain";
    }

    public static void main(String[] args) throws Exception {
        new RepoChain().start(args);
    }
}
