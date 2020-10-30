package com.sollyu.android.bluetooth.helper.network

import com.sollyu.android.bluetooth.helper.bean.ApiGithubContentsBean
import com.sollyu.android.bluetooth.helper.bean.ApiGithubReleasesBean
import com.sollyu.android.bluetooth.helper.bean.ApiGithubTreesBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Github常用API
 * @author sollyu
 * @date 2020-10-30
 */
interface NetworkGithub {

    /**
     * 查看最新的Release版本
     *
     * @param owner 项目人
     * @param repo 仓库名称
     */
    @GET(value = "/repos/{owner}/{repo}/releases/latest")
    @Headers(value = ["Accept:application/json", "Content-Type:application/json"])
    fun releaseLatest(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repo: String
    ): Observable<ApiGithubReleasesBean>

    /**
     * 根据版本获取详细内容
     *
     * @param owner 项目人
     * @param repo  仓库名称
     * @param tag   标签
     */
    @GET(value = "/repos/{owner}/{repo}/releases/tags/{tag}")
    @Headers(value = ["Accept:application/json", "Content-Type:application/json"])
    fun releaseTag(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repo: String,
        @Path(value = "tag") tag: String
    ): Observable<ApiGithubReleasesBean>

    /**
     * 获取仓库的文件列表
     *
     * @param owner     项目人
     * @param repo      仓库名称
     * @param branch    分支
     * @param recursive 固定1
     */
    @GET(value = "/repos/{owner}/{repo}/git/trees/{branch}")
    @Headers(value = ["Accept:application/json", "Content-Type:application/json"])
    fun trees(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repo: String,
        @Path(value = "branch") branch: String,
        @Query(value = "recursive") recursive: Int = 1
    ): Observable<ApiGithubTreesBean>

    /**
     * 获取文件的内容
     *
     * @param owner 项目人
     * @param repo  仓库名称
     * @param path  文件路径
     * @param ref   branch|commit等标示
     */
    @GET(value = "/repos/{owner}/{repo}/contents/{path}")
    @Headers(value = ["Accept:application/json", "Content-Type:application/json"])
    fun contents(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repo: String,
        @Path(value = "path") path: String,
        @Query(value = "ref") ref: String
    ): Observable<ApiGithubContentsBean>
}
