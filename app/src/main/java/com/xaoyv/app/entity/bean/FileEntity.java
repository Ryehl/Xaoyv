package com.xaoyv.app.entity.bean;

import java.util.List;

/**
 * Tag:
 *
 * @author Xaoyv
 * date 4/7/2021 6:58 PM
 */
public class FileEntity {

    /**
     * code : 200
     * message : ????
     * fileNames : ["http://xaoyv.com:8082/xaoyv/download?filename=1617151386Cache_-f963817513bcdae_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617151659Cache_-10ede1854d8a28a7_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617293667Cache_-60d2889e623b7c63_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617293668Cache_-18b11276585cad89_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617326958Cache_-3136a8900903393b_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617605585Cache_2daa1a130ea0a12a_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617605587Cache_-77bc9819cf735705_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617727988Cache_-124cb644046253e6_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617728050Cache_106a51ad6d928a24_fp.jpg.jpg","http://xaoyv.com:8082/xaoyv/download?filename=1617785190IMG_20210328_203651.jpg"]
     */

    private int code;
    private String message;
    private List<String> fileNames;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}
