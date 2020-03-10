<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="图片地址" prop="imgUrl">
        <el-input v-model="dataForm.imgUrl" placeholder="图片地址" hidden></el-input>
      </el-form-item>
      <el-upload
        class="avatar-uploader"
        :action="uploadUrl"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        style="text-align: center;">
        <img v-if="imageUrl" :src="imageUrl" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        <div class="el-upload__tip" slot="tip">只支持jpg、png、gif格式的图片！</div>
      </el-upload>
      <el-form-item label="广告链接" prop="linkUrl">
        <el-input v-model="dataForm.linkUrl" placeholder="广告链接"></el-input>
      </el-form-item>
      <el-form-item label="是否显示" prop="isShow">
        <template>
          <el-switch
            v-model="dataForm.isShow"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </template>
      </el-form-item>

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>

<script>
  export default {
    data () {
      return {
        imageUrl: '',
        uploadUrl: '',
        visible: false,
        dataForm: {
          id: 0,
          key: '',
          imgUrl: '',
          linkUrl: '',
          isShow: true
        },
        dataRule: {
          imgUrl: [
            { required: true, message: '图片地址不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.uploadUrl = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/support/screenAd/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.key = data.screenAd.key
                this.dataForm.imgUrl = data.screenAd.imgUrl
                this.dataForm.linkUrl = data.screenAd.linkUrl
                this.imageUrl = data.screenAd.imgUrl
                this.dataForm.isShow = data.screenAd.isShow === 0
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        console.info(this.dataForm.isShow)
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/support/screenAd/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'key': this.dataForm.key,
                'imgUrl': this.dataForm.imgUrl,
                'linkUrl': this.dataForm.linkUrl,
                'isShow': this.dataForm.isShow === true ? 0 : 1
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 上传成功
      handleAvatarSuccess (res, file) {
        this.imageUrl = URL.createObjectURL(file.raw)
        this.dataForm.imgUrl = res.url
      },
      // 上传之前
      beforeAvatarUpload (file) {
        if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
          this.$message.error('只支持jpg、png、gif格式的图片！')
          return false
        }
      }
    }
  }
</script>
