package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CategoryEntity
 * @Description: TODO
 * @Author jinluying
 * @Date 2020/8/27
 * @Version V1.0
 **/
@ApiModel(value = "分类实体类")
@Table(name="tb_category")
@Data
public class CategoryEntity {

    @Id
    @ApiModelProperty(value="分类主键",example = "1")
    //此处要引出来一个分组的概念,就是当前参数校验属于哪个组
    //有的操作不需要验证次参数就比如说新增就不需要校验id,但是修改需要
    @NotNull(message = "主键不能为空",groups = {MrOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "类目名称")
    @NotNull(message = "类目名称不能为空",groups = {MrOperation.Add.class ,MrOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "父类目id",example = "0")
    @NotNull(message = "父类目id不能为空",groups = {MrOperation.Add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否为父类节点,0为否,1为是",example = "1")
    @NotNull(message = "父类节点不能为空" ,groups = {MrOperation.Add.class})
    private Integer isParent;

    @ApiModelProperty(value="排序指数",example = "1")
    @NotNull(message="排序指数不能为空" ,groups = {MrOperation.Add.class})
    private Integer sort;
}
