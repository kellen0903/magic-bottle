<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.id" placeholder="用户ID" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.nickName" placeholder="用户昵称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.imei" placeholder="IMEI" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.status" clearable placeholder="禁言状态"  width="small">
          <el-option
            v-for="item in options"
            :key="item.status"
            :label="item.statusName"
            :value="item.status">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <!-- <el-button v-if="isAuth('user:frontUser:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button> -->
        <el-button v-if="isAuth('user:frontUser:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        label="用户ID">
      </el-table-column>
      <el-table-column
        prop="nickName"
        header-align="center"
        align="center"
        label="用户昵称">
      </el-table-column>
      <el-table-column
        prop="sex"
        header-align="center"
        align="center"
        label="性别"
        width="70">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.sex === 1" size="small">男</el-tag>
          <el-tag v-else-if="scope.row.sex === 2" size="small">女</el-tag>
          <el-tag v-else size="small">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="mobileName"
        header-align="center"
        align="center"
        label="手机名称">
      </el-table-column>
      <el-table-column
        prop="mobileVersion"
        header-align="center"
        align="center"
        label="手机型号">
      </el-table-column>
      <el-table-column
        prop="imei"
        header-align="center"
        align="center"
        label="imei">
      </el-table-column>
      <el-table-column
        prop="sourceType"
        header-align="center"
        align="center"
        label="来源"
        width="70">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.sourceType === 1" size="small">ios</el-tag>
          <el-tag v-else size="small">安卓</el-tag>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="lastLoginTime"
        header-align="center"
        align="center"
        label="最后登录时间"
        width="160">
      </el-table-column> -->
      <el-table-column
        prop="loginType"
        header-align="center"
        align="center"
        label="登录方式">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.loginType === 1" size="small">微信</el-tag>
          <el-tag v-else size="small">QQ</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="deleted"
        header-align="center"
        align="center"
        label="是否删除">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.deleted === 2" size="small" type="danger">是</el-tag>
          <el-tag v-else size="small">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="账户禁言状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 2" size="small" type="danger">禁言</el-tag>
          <el-tag v-else size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
          <el-button v-if="scope.row.status === 1" type="text" size="small" @click="banUser(scope.row.id)">账户禁言</el-button>
          <el-button v-if="scope.row.status === 2" type="text" size="small" @click="liftBan(scope.row.id)">账户解禁</el-button>
          <el-button v-if="scope.row.deviceStatus === 1"  type="text" size="small" @click="banDevice(scope.row.id)">设备禁言</el-button>
          <el-button v-if="scope.row.deviceStatus === 2"  type="text" size="small" @click="liftBanDevice(scope.row.id)">设备解禁</el-button>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="lastLoginTime"
        header-align="center"
        align="center"
        label="最后登录时间"
        width="160">
      </el-table-column> -->
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
  import AddOrUpdate from './frontUser-add-or-update'
  export default {
    data () {
      return {
        dataForm: {
          id: '',
          nickName: '',
          status: '',
          imei: ''
        },
        options: [
          {
            status: 1,
            statusName: '未禁言'
          },
          {
            status: 2,
            statusName: '已禁言'
          }
        ],
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false
      }
    },
    components: {
      AddOrUpdate
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/user/front-user/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'id': this.dataForm.id,
            'nickName': this.dataForm.nickName,
            'status': this.dataForm.status,
            'imei': this.dataForm.imei
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 新增 / 修改
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      // 删除
      deleteHandle (id) {
        var userIds = id ? [id] : this.dataListSelections.map(item => {
          return item.userId
        })
        this.$confirm(`确定对[id=${userIds.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/user/front-user/delete'),
            method: 'post',
            data: this.$http.adornData(userIds, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },
      banUser (id) {
        this.$confirm(`确定禁言用户`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/user/front-user/ban/${id}`),
            method: 'put',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },
      liftBan (id) {
        this.$confirm(`确定解禁用户`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/user/front-user/liftBan/${id}`),
            method: 'put',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },
      banDevice (id) {
        this.$confirm(`确定禁言设备`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/user/front-user/banDevice/${id}`),
            method: 'put',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },
      liftBanDevice (id) {
        this.$confirm(`确定解禁设备`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/user/front-user/liftBanDevice/${id}`),
            method: 'put',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      }
    }
  }
</script>
