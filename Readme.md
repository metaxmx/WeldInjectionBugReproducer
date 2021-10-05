# Minimal Reproducer Example for an Injection Bug

## Bug Description

Injection of `jakarta.servlet.http.HttpServletRequest` (and other classes of the Jakarta Servlet Module) fails on Tomcat10 with Weld4.

```java
@Named("test")
@RequestScoped
public class TestBean {

    @Inject
    private HttpServletRequest request;

    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }

}
```

```xhtml
 <h:outputText value="#{test.userAgent}"/>
```

### To reproduce the error:

Build WAR file of project and deploy to a default Tomcat 10 instance (Java 17 used in example).
The open the index page of the deployment `/index.html`.

### Expected Behaviour

The index page shows the User Agent (from Servlet Request).

### Actual Behaviour

During Injection (or better: during Proxy generation of the `HttpServletRequest` instance) an error occurs:

```
	org.jboss.weld.exceptions.WeldException: WELD-001524: Unable to load proxy class for bean WELD%AbstractBuiltInBean%_/WEB-INF/classes%HttpServletRequest with class class java.lang.Object
		at org.jboss.weld.bean.proxy.ProxyFactory.getProxyClass(ProxyFactory.java:406)
		at org.jboss.weld.bean.proxy.ProxyFactory.run(ProxyFactory.java:359)
		at org.jboss.weld.bean.proxy.ProxyFactory.create(ProxyFactory.java:351)
		at org.jboss.weld.bean.proxy.ClientProxyFactory.create(ClientProxyFactory.java:83)
		at org.jboss.weld.bean.proxy.ClientProxyProvider.createClientProxy(ClientProxyProvider.java:205)
		at org.jboss.weld.bean.proxy.ClientProxyProvider.createClientProxy(ClientProxyProvider.java:195)
		at org.jboss.weld.bean.proxy.ClientProxyProvider.access$100(ClientProxyProvider.java:44)
		at org.jboss.weld.bean.proxy.ClientProxyProvider$CreateClientProxy.apply(ClientProxyProvider.java:52)
		at org.jboss.weld.bean.proxy.ClientProxyProvider$CreateClientProxy.apply(ClientProxyProvider.java:48)
		at org.jboss.weld.util.cache.ReentrantMapBackedComputingCache.lambda$new$0(ReentrantMapBackedComputingCache.java:55)
		at org.jboss.weld.util.LazyValueHolder$1.computeValue(LazyValueHolder.java:32)
		at org.jboss.weld.util.LazyValueHolder.get(LazyValueHolder.java:46)
		at org.jboss.weld.util.cache.ReentrantMapBackedComputingCache.getValue(ReentrantMapBackedComputingCache.java:72)
		at org.jboss.weld.util.cache.ReentrantMapBackedComputingCache.getCastValue(ReentrantMapBackedComputingCache.java:78)
		at org.jboss.weld.bean.proxy.ClientProxyProvider.getClientProxy(ClientProxyProvider.java:229)
		at org.jboss.weld.manager.BeanManagerImpl.getReference(BeanManagerImpl.java:688)
		at org.jboss.weld.manager.BeanManagerImpl.getInjectableReference(BeanManagerImpl.java:794)
		at org.jboss.weld.injection.FieldInjectionPoint.inject(FieldInjectionPoint.java:92)
		at org.jboss.weld.util.Beans.injectBoundFields(Beans.java:345)
		at org.jboss.weld.util.Beans.injectFieldsAndInitializers(Beans.java:356)
		at org.jboss.weld.injection.producer.ResourceInjector$1.proceed(ResourceInjector.java:69)
		at org.jboss.weld.injection.InjectionContextImpl.run(InjectionContextImpl.java:48)
		at org.jboss.weld.injection.producer.ResourceInjector.inject(ResourceInjector.java:71)
		at org.jboss.weld.injection.producer.BasicInjectionTarget.inject(BasicInjectionTarget.java:117)
		at org.jboss.weld.bean.ManagedBean.create(ManagedBean.java:161)
		at org.jboss.weld.contexts.AbstractContext.get(AbstractContext.java:96)
		at org.jboss.weld.bean.ContextualInstanceStrategy$DefaultContextualInstanceStrategy.get(ContextualInstanceStrategy.java:100)
		at org.jboss.weld.bean.ContextualInstanceStrategy$CachingContextualInstanceStrategy.get(ContextualInstanceStrategy.java:177)
		at org.jboss.weld.bean.ContextualInstance.get(ContextualInstance.java:50)
		at org.jboss.weld.manager.BeanManagerImpl.getReference(BeanManagerImpl.java:694)
		at org.jboss.weld.module.web.el.AbstractWeldELResolver.lookup(AbstractWeldELResolver.java:107)
		at org.jboss.weld.module.web.el.AbstractWeldELResolver.getValue(AbstractWeldELResolver.java:90)
		at org.jboss.weld.environment.servlet.util.ForwardingELResolver.getValue(ForwardingELResolver.java:49)
		at jakarta.el.CompositeELResolver.getValue(CompositeELResolver.java:62)
		at com.sun.faces.el.DemuxCompositeELResolver._getValue(DemuxCompositeELResolver.java:139)
		at com.sun.faces.el.DemuxCompositeELResolver.getValue(DemuxCompositeELResolver.java:164)
		at org.apache.el.parser.AstIdentifier.getValue(AstIdentifier.java:93)
		at org.apache.el.parser.AstValue.getValue(AstValue.java:136)
		at org.apache.el.ValueExpressionImpl.getValue(ValueExpressionImpl.java:189)
		at org.jboss.weld.module.web.el.WeldValueExpression.getValue(WeldValueExpression.java:50)
		at com.sun.faces.facelets.el.TagValueExpression.getValue(TagValueExpression.java:73)
		at jakarta.faces.component.ComponentStateHelper.eval(ComponentStateHelper.java:188)
		at jakarta.faces.component.ComponentStateHelper.eval(ComponentStateHelper.java:175)
		at jakarta.faces.component.UIOutput.getValue(UIOutput.java:134)
		at com.sun.faces.renderkit.html_basic.HtmlBasicInputRenderer.getValue(HtmlBasicInputRenderer.java:163)
		at com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.getCurrentValue(HtmlBasicRenderer.java:313)
		at com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.encodeEnd(HtmlBasicRenderer.java:142)
		at jakarta.faces.component.UIComponentBase.encodeEnd(UIComponentBase.java:587)
		at jakarta.faces.component.UIComponent.encodeAll(UIComponent.java:1460)
		at jakarta.faces.render.Renderer.encodeChildren(Renderer.java:141)
		at jakarta.faces.component.UIComponentBase.encodeChildren(UIComponentBase.java:558)
		at jakarta.faces.component.UIComponent.encodeAll(UIComponent.java:1453)
		at jakarta.faces.component.UIComponent.encodeAll(UIComponent.java:1456)
		at jakarta.faces.component.UIComponent.encodeAll(UIComponent.java:1456)
		at com.sun.faces.application.view.FaceletViewHandlingStrategy.renderView(FaceletViewHandlingStrategy.java:458)
		at com.sun.faces.application.view.MultiViewHandler.renderView(MultiViewHandler.java:164)
		at jakarta.faces.application.ViewHandlerWrapper.renderView(ViewHandlerWrapper.java:125)
		at com.sun.faces.lifecycle.RenderResponsePhase.execute(RenderResponsePhase.java:93)
		at com.sun.faces.lifecycle.Phase.doPhase(Phase.java:72)
		at com.sun.faces.lifecycle.LifecycleImpl.render(LifecycleImpl.java:178)
		at jakarta.faces.webapp.FacesServlet.executeLifecyle(FacesServlet.java:682)
		at jakarta.faces.webapp.FacesServlet.service(FacesServlet.java:437)
		at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:223)
		at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158)
		at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
		at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185)
		at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158)
		at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197)
		at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
		at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)
		at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:119)
		at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
		at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:690)
		at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
		at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:353)
		at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:382)
		at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
		at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:872)
		at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1695)
		at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
		at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)
		at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
		at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
		at java.base/java.lang.Thread.run(Thread.java:833)
	Caused by: java.lang.NoClassDefFoundError: org/jboss/weld/proxy/WeldConstruct
		at java.base/java.lang.ClassLoader.defineClass0(Native Method)
		at java.base/java.lang.System$2.defineClass(System.java:2307)
		at java.base/java.lang.invoke.MethodHandles$Lookup$ClassDefiner.defineClass(MethodHandles.java:2439)
		at java.base/java.lang.invoke.MethodHandles$Lookup$ClassDefiner.defineClass(MethodHandles.java:2416)
		at java.base/java.lang.invoke.MethodHandles$Lookup.defineClass(MethodHandles.java:1843)
		at org.jboss.weld.bean.proxy.util.WeldDefaultProxyServices.defineWithMethodLookup(WeldDefaultProxyServices.java:163)
		at org.jboss.weld.bean.proxy.util.WeldDefaultProxyServices.defineClass(WeldDefaultProxyServices.java:69)
		at org.jboss.weld.bean.proxy.ProxyFactory.toClass(ProxyFactory.java:934)
		at org.jboss.weld.bean.proxy.ProxyFactory.createProxyClass(ProxyFactory.java:492)
		at org.jboss.weld.bean.proxy.ProxyFactory.getProxyClass(ProxyFactory.java:398)
		... 83 more
	Caused by: java.lang.ClassNotFoundException: org.jboss.weld.proxy.WeldConstruct
		at java.base/java.net.URLClassLoader.findClass(URLClassLoader.java:440)
		at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:587)
		at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
		... 93 more
```