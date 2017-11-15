### ARouter-Ex

	对原有的ARouter进行了扩展，将跳转像Retrofit一样了

1.对于原有的ARouter进行了扩展，因为原来的写法中，所有的路由都是手动配置的，而且其url没有一个常量进行维护，字段也是相当于自己定义好了。虽然很大程度上这样的写法没有问题，但是还是有一些小毛病的，比如，修改Activity的url地址或者删除了url地址之后，面临着所有的跳转都需要修改，但是如果不是由常量维护的话，那么就存在一个问题。

在模块化开发的过程中，A在开发A模块，B在开发B模块，A模块删除了一个Activity，忘记通知B了，但是B中有跳转逻辑并且需要跳转到被删除的页面中，这时候，由于是手写的url，不方便，因此可以进行采用常量的形式进行，但是常量需要放在底层模块中，这样大家才能够共享，频繁的手动修改底层模块，比较麻烦，因而思路是使用AnnotationProcessor在编译过程中将使用到的ARoute的页面，进行自动化生成全局的静态变量，相于是配置了一个路由表，因此这样就比较方便了。

2.结合了OkHttp和Retrofit的思路,ARouter相当于okhttp，我们的max相当于retrofit（PS：这里有些夸张了，我这里的设计Retrofit的思想都不是一个层级的），因此跳转使用了动态代理

例如跳转页面只需要调用如下语句即可，并且传递了参数。

````
    RouteApi routeApi = Pitaya.create(RouteApi.class);
    routeApi.requestTestModule(123456, MainActivity.this)
            .subscribe(new Consumer<ResultWrapper>() {
                @Override
                public void accept(ResultWrapper resultWrapper) throws Exception {
                    Toast.makeText(MainActivity.this, resultWrapper + "", Toast.LENGTH_LONG).show();
                }
            });


````